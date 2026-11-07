package com.finanzafamiliar.contabilidad.model;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

/**
 * Resumen financiero del hogar que se muestra en la pagina de inicio del
 * front-end: total de ingresos, total de gastos, saldo y cantidad de movimientos.
 *
 * Es un "record" de Java: una clase inmutable que solo guarda datos. Java crea
 * automaticamente el constructor, los metodos de lectura, equals y toString.
 */
@Schema(description = "Resumen financiero del hogar")
public record ResumenMovimientos(
        @Schema(description = "Suma de todos los ingresos", example = "2500000.00")
        BigDecimal totalIngresos,
        @Schema(description = "Suma de todos los gastos", example = "920000.00")
        BigDecimal totalGastos,
        @Schema(description = "Ingresos menos gastos", example = "1580000.00")
        BigDecimal saldo,
        @Schema(description = "Cantidad de movimientos registrados", example = "3")
        long cantidadMovimientos
) {
}
