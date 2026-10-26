package com.finanzafamiliar.contabilidad.exception;

/**
 * Se lanza cuando la operacion choca con datos que ya existen: un correo
 * repetido, o eliminar un usuario que tiene movimientos registrados.
 * El ManejadorExcepciones la convierte en HTTP 409 (Conflict).
 */
public class ConflictoException extends RuntimeException {

    public ConflictoException(String mensaje) {
        super(mensaje);
    }
}
