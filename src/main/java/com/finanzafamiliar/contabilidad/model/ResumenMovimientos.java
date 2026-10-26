package com.finanzafamiliar.contabilidad.model;

import java.math.BigDecimal;

/**
 * Resumen financiero del hogar que se muestra en la pagina de inicio del
 * front-end: total de ingresos, total de gastos, saldo y cantidad de movimientos.
 *
 * Es un "record" de Java: una clase inmutable que solo guarda datos. Java crea
 * automaticamente el constructor, los metodos de lectura, equals y toString.
 */
public record ResumenMovimientos(
        BigDecimal totalIngresos,
        BigDecimal totalGastos,
        BigDecimal saldo,
        long cantidadMovimientos
) {
}
