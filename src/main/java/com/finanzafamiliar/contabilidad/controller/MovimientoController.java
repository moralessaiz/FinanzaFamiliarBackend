package com.finanzafamiliar.contabilidad.controller;

import com.finanzafamiliar.contabilidad.config.OpenApiConfig;
import com.finanzafamiliar.contabilidad.model.Movimiento;
import com.finanzafamiliar.contabilidad.model.RespuestaEliminacion;
import com.finanzafamiliar.contabilidad.model.RespuestaError;
import com.finanzafamiliar.contabilidad.model.ResumenMovimientos;
import com.finanzafamiliar.contabilidad.service.MovimientoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Servicios REST del modulo de Movimientos (CRUD de ingresos y gastos).
 *
 * Reemplaza a ListarMovimientosServlet y RegistrarMovimientoServlet.
 *
 * Las anotaciones @Operation, @Parameter y @ApiResponse documentan cada servicio
 * en Swagger UI (http://localhost:8080/swagger-ui.html).
 *
 * | Metodo | Ruta                         | Antes (Servlets)                |
 * |--------|------------------------------|---------------------------------|
 * | GET    | /api/movimientos?tipo=       | GET  /movimientos?tipo=         |
 * | GET    | /api/movimientos/resumen     | (no existia)                    |
 * | GET    | /api/movimientos/{id}        | (no existia)                    |
 * | POST   | /api/movimientos             | POST /movimientos/registrar     |
 * | PUT    | /api/movimientos/{id}        | (solo en la prueba de consola)  |
 * | DELETE | /api/movimientos/{id}        | (solo en la prueba de consola)  |
 */
@RestController
@RequestMapping("/api/movimientos")
@Tag(name = OpenApiConfig.TAG_MOVIMIENTOS)
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    /** READ - Lista los movimientos; admite el filtro opcional ?tipo=ingreso, gasto o todos. */
    @Operation(summary = "Listar movimientos",
            description = "Devuelve los movimientos del más reciente al más antiguo, con el nombre de su categoría "
                    + "y del usuario que lo registró. Se puede filtrar por tipo con el parámetro `tipo`.")
    @ApiResponse(responseCode = "200", description = "Lista de movimientos (puede estar vacía)")
    @ApiResponse(responseCode = "400", description = "El filtro tipo no es ingreso, gasto ni todos",
            content = @Content(schema = @Schema(implementation = RespuestaError.class),
                    examples = @ExampleObject(value = EjemplosApi.MOVIMIENTO_FILTRO_INVALIDO)))
    @GetMapping
    public List<Movimiento> listarMovimientos(
            @Parameter(description = "Filtro opcional por tipo", example = "gasto",
                    schema = @Schema(allowableValues = {"ingreso", "gasto", "todos"}))
            @RequestParam(required = false) String tipo) {
        return movimientoService.listarMovimientos(tipo);
    }

    /** READ - Total de ingresos, total de gastos y saldo del hogar. */
    @Operation(summary = "Consultar el resumen financiero",
            description = "Devuelve el total de ingresos, el total de gastos, el saldo (ingresos menos gastos) "
                    + "y la cantidad de movimientos. El front-end lo muestra en la página de inicio.")
    @ApiResponse(responseCode = "200", description = "Resumen calculado")
    @GetMapping("/resumen")
    public ResumenMovimientos consultarResumen() {
        return movimientoService.consultarResumen();
    }

    /** READ - Consulta un movimiento por su id. */
    @Operation(summary = "Consultar un movimiento", description = "Devuelve los datos de un movimiento a partir de su id.")
    @ApiResponse(responseCode = "200", description = "Movimiento encontrado")
    @ApiResponse(responseCode = "400", description = "El id no es un número",
            content = @Content(schema = @Schema(implementation = RespuestaError.class),
                    examples = @ExampleObject(value = EjemplosApi.ID_INVALIDO)))
    @ApiResponse(responseCode = "404", description = "No existe un movimiento con ese id",
            content = @Content(schema = @Schema(implementation = RespuestaError.class),
                    examples = @ExampleObject(value = EjemplosApi.MOVIMIENTO_NO_ENCONTRADO)))
    @GetMapping("/{id}")
    public Movimiento buscarMovimiento(@Parameter(description = "Id del movimiento", example = "12") @PathVariable Long id) {
        return movimientoService.buscarPorId(id);
    }

    /** CREATE - Registra un movimiento nuevo. Responde 201 (Created). */
    @Operation(summary = "Registrar un movimiento",
            description = "Registra un ingreso o un gasto. Reglas: la categoría y el usuario deben existir, y la "
                    + "categoría debe ser del mismo tipo del movimiento (por ejemplo, *Salario* solo sirve para ingresos). "
                    + "No se envían `idMovimiento`, `nombreCategoria` ni `nombreUsuario`.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = EjemplosApi.MOVIMIENTO_NUEVO)))
    @ApiResponse(responseCode = "201", description = "Movimiento creado; devuelve el movimiento con su id",
            content = @Content(schema = @Schema(implementation = Movimiento.class),
                    examples = @ExampleObject(value = EjemplosApi.MOVIMIENTO_CREADO)))
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio no cumplida",
            content = @Content(schema = @Schema(implementation = RespuestaError.class), examples = {
                    @ExampleObject(name = "Datos inválidos", value = EjemplosApi.MOVIMIENTO_DATOS_INVALIDOS),
                    @ExampleObject(name = "Categoría de otro tipo", value = EjemplosApi.MOVIMIENTO_CATEGORIA_OTRO_TIPO)}))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Movimiento crearMovimiento(@Valid @RequestBody Movimiento movimiento) {
        return movimientoService.crearMovimiento(movimiento);
    }

    /** UPDATE - Actualiza un movimiento existente. */
    @Operation(summary = "Actualizar un movimiento",
            description = "Reemplaza todos los datos de un movimiento existente. Aplican las mismas reglas del registro.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = EjemplosApi.MOVIMIENTO_NUEVO)))
    @ApiResponse(responseCode = "200", description = "Movimiento actualizado; devuelve los datos nuevos")
    @ApiResponse(responseCode = "400", description = "Datos inválidos o regla de negocio no cumplida",
            content = @Content(schema = @Schema(implementation = RespuestaError.class), examples = {
                    @ExampleObject(name = "Datos inválidos", value = EjemplosApi.MOVIMIENTO_DATOS_INVALIDOS),
                    @ExampleObject(name = "Categoría de otro tipo", value = EjemplosApi.MOVIMIENTO_CATEGORIA_OTRO_TIPO)}))
    @ApiResponse(responseCode = "404", description = "No existe un movimiento con ese id",
            content = @Content(schema = @Schema(implementation = RespuestaError.class),
                    examples = @ExampleObject(value = EjemplosApi.MOVIMIENTO_NO_ENCONTRADO)))
    @PutMapping("/{id}")
    public Movimiento actualizarMovimiento(
            @Parameter(description = "Id del movimiento", example = "12") @PathVariable Long id,
            @Valid @RequestBody Movimiento movimiento) {
        return movimientoService.actualizarMovimiento(id, movimiento);
    }

    /** DELETE - Elimina un movimiento. Responde 200 (OK) con un mensaje de confirmacion y los datos eliminados. */
    @Operation(summary = "Eliminar un movimiento",
            description = "Elimina un movimiento y devuelve un mensaje de confirmación con los datos que tenía.")
    @ApiResponse(responseCode = "200", description = "Movimiento eliminado; devuelve un mensaje de confirmación y los datos que tenía")
    @ApiResponse(responseCode = "404", description = "No existe un movimiento con ese id",
            content = @Content(schema = @Schema(implementation = RespuestaError.class),
                    examples = @ExampleObject(value = EjemplosApi.MOVIMIENTO_NO_ENCONTRADO)))
    @DeleteMapping("/{id}")
    public RespuestaEliminacion<Movimiento> eliminarMovimiento(
            @Parameter(description = "Id del movimiento", example = "12") @PathVariable Long id) {
        Movimiento eliminado = movimientoService.eliminarMovimiento(id);
        return new RespuestaEliminacion<>(
                "Se eliminó el movimiento \"" + eliminado.getConcepto() + "\" (id " + id + ")", eliminado);
    }
}
