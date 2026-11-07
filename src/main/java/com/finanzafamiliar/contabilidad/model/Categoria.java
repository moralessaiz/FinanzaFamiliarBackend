package com.finanzafamiliar.contabilidad.model;

import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Clase modelo (POJO) que representa una categoria de ingreso o gasto
 * (por ejemplo Salario o Mercado). Corresponde a la tabla "categoria".
 */
@Schema(description = "Categoría de ingreso o gasto")
public class Categoria {

    @Schema(description = "Identificador de la categoría", example = "3")
    private Long idCategoria;
    @Schema(description = "Nombre de la categoría", example = "Mercado")
    private String nombre;
    @Schema(description = "Tipo de movimientos que clasifica", example = "gasto", allowableValues = {"ingreso", "gasto"})
    private String tipo;

    public Categoria() {
    }

    public Categoria(Long idCategoria, String nombre, String tipo) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public Long getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(Long idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
