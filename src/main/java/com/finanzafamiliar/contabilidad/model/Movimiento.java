package com.finanzafamiliar.contabilidad.model;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Clase modelo (POJO) que representa un movimiento (ingreso o gasto)
 * del Sistema de Contabilidad Familiar - FinanzaFamiliar.
 * Corresponde a la tabla "movimiento".
 *
 * Los campos nombreCategoria y nombreUsuario son solo de lectura: se llenan
 * al consultar (con un JOIN) para que el front-end muestre los nombres en
 * lugar de los numeros de id.
 */
@Schema(description = "Ingreso o gasto del hogar")
public class Movimiento {

    @Schema(description = "Identificador asignado por la base de datos. No se envía al crear", example = "12", accessMode = Schema.AccessMode.READ_ONLY)
    private Long idMovimiento;

    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(regexp = "ingreso|gasto", message = "El tipo debe ser ingreso o gasto")
    @Schema(description = "Tipo de movimiento. Debe coincidir con el tipo de la categoría", example = "gasto",
            allowableValues = {"ingreso", "gasto"}, requiredMode = Schema.RequiredMode.REQUIRED)
    private String tipo;

    // DECIMAL(12,2) en MySQL: maximo 10 digitos enteros y 2 decimales
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero")
    @Digits(integer = 10, fraction = 2, message = "El monto admite máximo 10 dígitos enteros y 2 decimales")
    @Schema(description = "Valor en pesos. Mayor que cero, máximo 10 dígitos enteros y 2 decimales", example = "450000.00",
            minimum = "0.01", requiredMode = Schema.RequiredMode.REQUIRED)
    private BigDecimal monto;

    @NotBlank(message = "El concepto es obligatorio")
    @Size(max = 150, message = "El concepto no puede superar 150 caracteres")
    @Schema(description = "Descripción del movimiento, máximo 150 caracteres", example = "Mercado del mes",
            requiredMode = Schema.RequiredMode.REQUIRED, maxLength = 150)
    private String concepto;

    @NotNull(message = "La fecha es obligatoria")
    @PastOrPresent(message = "La fecha no puede ser posterior a hoy")
    @Schema(description = "Fecha del movimiento (aaaa-mm-dd). No puede ser posterior a hoy", example = "2026-11-01",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private LocalDate fecha;

    @NotNull(message = "La categoría es obligatoria")
    @Schema(description = "Id de una categoría existente del mismo tipo (ver GET /api/categorias)", example = "3",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long idCategoria;

    @NotNull(message = "El usuario es obligatorio")
    @Schema(description = "Id del usuario que registra el movimiento (debe existir)", example = "1",
            requiredMode = Schema.RequiredMode.REQUIRED)
    private Long idUsuario;

    /** Solo de lectura: nombre de la categoria, obtenido al consultar. */
    @Schema(description = "Nombre de la categoría. Solo se llena al consultar", example = "Mercado", accessMode = Schema.AccessMode.READ_ONLY)
    private String nombreCategoria;

    /** Solo de lectura: nombre del usuario que registro el movimiento, obtenido al consultar. */
    @Schema(description = "Nombre del usuario que lo registró. Solo se llena al consultar", example = "Amalfi Aya", accessMode = Schema.AccessMode.READ_ONLY)
    private String nombreUsuario;

    public Movimiento() {
    }

    public Long getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Long idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public BigDecimal getMonto() {
        return monto;
    }

    public void setMonto(BigDecimal monto) {
        this.monto = monto;
    }

    public String getConcepto() {
        return concepto;
    }

    public void setConcepto(String concepto) {
        this.concepto = concepto;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public Long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public String getNombreUsuario() {
        return nombreUsuario;
    }

    public void setNombreUsuario(String nombreUsuario) {
        this.nombreUsuario = nombreUsuario;
    }

    @Override
    public String toString() {
        return "Movimiento{" +
                "idMovimiento=" + idMovimiento +
                ", tipo='" + tipo + '\'' +
                ", monto=" + monto +
                ", concepto='" + concepto + '\'' +
                ", fecha=" + fecha +
                ", idCategoria=" + idCategoria +
                ", idUsuario=" + idUsuario +
                '}';
    }
}
