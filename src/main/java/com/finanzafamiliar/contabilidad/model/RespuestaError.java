package com.finanzafamiliar.contabilidad.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.Map;

/**
 * Cuerpo JSON de todas las respuestas de error de la API (lo arma ManejadorExcepciones):
 *
 * <pre>
 * { "mensaje": "Datos inválidos", "errores": { "monto": "El monto debe ser mayor que cero" } }
 * </pre>
 *
 * El campo "errores" solo se incluye cuando hay campos con datos invalidos.
 */
@Schema(description = "Respuesta de error. Siempre trae un mensaje; si hay campos inválidos, trae también el detalle de cada uno")
public record RespuestaError(

        @Schema(description = "Descripción del error", example = "Datos inválidos")
        String mensaje,

        @Schema(description = "Error de cada campo inválido (nombre del campo → mensaje). Solo aparece en errores de validación",
                example = "{\"correo\": \"El correo electrónico no es válido\"}")
        @JsonInclude(JsonInclude.Include.NON_EMPTY)
        Map<String, String> errores
) {
}
