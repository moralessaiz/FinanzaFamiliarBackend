package com.finanzafamiliar.contabilidad.exception;

/**
 * Se lanza cuando se busca un usuario o movimiento que no existe.
 * El ManejadorExcepciones la convierte en una respuesta HTTP 404 (Not Found).
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
