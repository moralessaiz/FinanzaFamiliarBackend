package com.finanzafamiliar.contabilidad.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Respuesta JSON de las operaciones DELETE: confirma con un mensaje que el
 * registro se elimino y devuelve los datos que tenia, por ejemplo:
 *
 * <pre>
 * { "mensaje": "Se eliminó el usuario Laura Morales (id 8)",
 *   "eliminado": { "idUsuario": 8, "nombre": "Laura Morales", ... } }
 * </pre>
 *
 * Usa el mismo campo "mensaje" de las respuestas de error, para que el
 * front-end y Postman lean siempre el mismo formato.
 *
 * @param <T> tipo del registro eliminado (Usuario o Movimiento).
 */
@Schema(description = "Confirmación de una eliminación con los datos que tenía el registro")
public record RespuestaEliminacion<T>(
        @Schema(description = "Mensaje de confirmación con el nombre y el id del registro eliminado. "
                + "Ejemplos: Se eliminó el usuario Laura Morales (id 8) / Se eliminó el movimiento \"Mercado del mes\" (id 12)")
        String mensaje,
        @Schema(description = "Datos que tenía el registro eliminado")
        T eliminado
) {
}
