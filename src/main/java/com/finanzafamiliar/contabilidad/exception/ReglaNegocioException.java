package com.finanzafamiliar.contabilidad.exception;

/**
 * Se lanza cuando los datos tienen el formato correcto pero no cumplen una
 * regla del negocio (por ejemplo, registrar un ingreso con la categoria "Mercado",
 * que es de gasto). El ManejadorExcepciones la convierte en HTTP 400 (Bad Request).
 */
public class ReglaNegocioException extends RuntimeException {

    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
