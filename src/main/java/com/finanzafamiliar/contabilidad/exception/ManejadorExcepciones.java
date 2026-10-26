package com.finanzafamiliar.contabilidad.exception;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Convierte los errores de la API en respuestas JSON claras, siempre con el
 * mismo formato, para que el front-end Angular las pueda mostrar al usuario:
 *
 * <pre>
 * { "mensaje": "Datos inválidos", "errores": { "monto": "El monto debe ser mayor que cero" } }
 * </pre>
 *
 * Cumple el papel que antes tenian los bloques catch de cada servlet.
 */
@RestControllerAdvice
public class ManejadorExcepciones {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManejadorExcepciones.class);

    /** 400 - Algun campo no cumple las anotaciones de validacion (@NotBlank, @Size, @Email...). */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> manejarValidacion(MethodArgumentNotValidException excepcion) {
        Map<String, String> errores = new LinkedHashMap<>();
        excepcion.getBindingResult().getFieldErrors()
                .forEach(error -> errores.putIfAbsent(error.getField(), error.getDefaultMessage()));
        return responder(HttpStatus.BAD_REQUEST, "Datos inválidos", errores);
    }

    /** 400 - El JSON esta mal escrito o un valor no tiene el tipo esperado (por ejemplo, una fecha invalida). */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> manejarJsonInvalido(HttpMessageNotReadableException excepcion) {
        if (excepcion.getCause() instanceof UnrecognizedPropertyException desconocido) {
            return responder(HttpStatus.BAD_REQUEST, "Datos inválidos",
                    Map.of(desconocido.getPropertyName(), "Este campo no existe"));
        }
        if (excepcion.getCause() instanceof MismatchedInputException formatoInvalido
                && !formatoInvalido.getPath().isEmpty()) {
            String campo = formatoInvalido.getPath().stream()
                    .map(JsonMappingException.Reference::getFieldName)
                    .collect(Collectors.joining("."));
            return responder(HttpStatus.BAD_REQUEST, "Datos inválidos",
                    Map.of(campo, "El valor no tiene un formato válido"));
        }
        return responder(HttpStatus.BAD_REQUEST, "El cuerpo de la petición no es un JSON válido", null);
    }

    /** 400 - Un parametro de la URL no tiene el tipo esperado (por ejemplo /api/usuarios/abc). */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> manejarParametroInvalido(MethodArgumentTypeMismatchException excepcion) {
        return responder(HttpStatus.BAD_REQUEST,
                "El parámetro '" + excepcion.getName() + "' no tiene un valor válido", null);
    }

    /** 400 - Los datos no cumplen una regla del negocio. */
    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, Object>> manejarReglaNegocio(ReglaNegocioException excepcion) {
        return responder(HttpStatus.BAD_REQUEST, excepcion.getMessage(), null);
    }

    /** 404 - El usuario o movimiento buscado no existe. */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> manejarNoEncontrado(RecursoNoEncontradoException excepcion) {
        return responder(HttpStatus.NOT_FOUND, excepcion.getMessage(), null);
    }

    /** 409 - La operacion choca con datos existentes (correo repetido, usuario con movimientos). */
    @ExceptionHandler(ConflictoException.class)
    public ResponseEntity<Map<String, Object>> manejarConflicto(ConflictoException excepcion) {
        return responder(HttpStatus.CONFLICT, excepcion.getMessage(), null);
    }

    /** 409 - MySQL rechazo la operacion por una llave foranea u otra restriccion de la tabla. */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, Object>> manejarIntegridad(DataIntegrityViolationException excepcion) {
        LOGGER.warn("Restriccion de la base de datos: {}", excepcion.getMostSpecificCause().getMessage());
        return responder(HttpStatus.CONFLICT,
                "La operación no cumple las reglas de la base de datos (datos relacionados o inexistentes)", null);
    }

    /** 503 - No hay conexion con MySQL (servicio apagado, usuario o clave incorrectos). */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<Map<String, Object>> manejarBaseDatos(DataAccessException excepcion) {
        LOGGER.error("Error de acceso a la base de datos", excepcion);
        return responder(HttpStatus.SERVICE_UNAVAILABLE,
                "No fue posible acceder a la base de datos. Verifique que MySQL esté encendido.", null);
    }

    /**
     * Cualquier otro error. Los errores que Spring ya clasifica (ruta inexistente 404,
     * metodo no permitido 405, etc.) conservan su codigo; los demas son 500 y se
     * registran en la consola para poder revisarlos.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarGeneral(Exception excepcion) {
        if (excepcion instanceof ErrorResponse errorSpring) {
            HttpStatus estado = HttpStatus.valueOf(errorSpring.getStatusCode().value());
            String mensaje = estado == HttpStatus.NOT_FOUND
                    ? "La ruta solicitada no existe"
                    : errorSpring.getBody().getDetail();
            return responder(estado, mensaje, null);
        }
        LOGGER.error("Error no controlado", excepcion);
        return responder(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrió un error inesperado en el servidor", null);
    }

    /** Arma el cuerpo JSON de la respuesta de error. */
    private ResponseEntity<Map<String, Object>> responder(HttpStatus estado, String mensaje, Map<String, String> errores) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("mensaje", mensaje);
        if (errores != null && !errores.isEmpty()) {
            cuerpo.put("errores", errores);
        }
        return ResponseEntity.status(estado).body(cuerpo);
    }
}
