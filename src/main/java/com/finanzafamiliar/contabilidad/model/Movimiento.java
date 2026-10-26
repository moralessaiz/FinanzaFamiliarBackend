package com.finanzafamiliar.contabilidad.model;

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
public class Movimiento {

    private Long idMovimiento;

    @NotBlank(message = "El tipo es obligatorio")
    @Pattern(regexp = "ingreso|gasto", message = "El tipo debe ser ingreso o gasto")
    private String tipo;

    // DECIMAL(12,2) en MySQL: maximo 10 digitos enteros y 2 decimales
    @NotNull(message = "El monto es obligatorio")
    @DecimalMin(value = "0.01", message = "El monto debe ser mayor que cero")
    @Digits(integer = 10, fraction = 2, message = "El monto admite máximo 10 dígitos enteros y 2 decimales")
    private BigDecimal monto;

    @NotBlank(message = "El concepto es obligatorio")
    @Size(max = 150, message = "El concepto no puede superar 150 caracteres")
    private String concepto;

    @NotNull(message = "La fecha es obligatoria")
    @PastOrPresent(message = "La fecha no puede ser posterior a hoy")
    private LocalDate fecha;

    @NotNull(message = "La categoría es obligatoria")
    private Long idCategoria;

    @NotNull(message = "El usuario es obligatorio")
    private Long idUsuario;

    /** Solo de lectura: nombre de la categoria, obtenido al consultar. */
    private String nombreCategoria;

    /** Solo de lectura: nombre del usuario que registro el movimiento, obtenido al consultar. */
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
