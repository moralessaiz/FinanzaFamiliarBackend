package com.finanzafamiliar.contabilidad.model;

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
public record RespuestaEliminacion<T>(
        String mensaje,
        T eliminado
) {
}
