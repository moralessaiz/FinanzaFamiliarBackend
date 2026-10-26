package com.finanzafamiliar.contabilidad.model;

/**
 * Clase modelo (POJO) que representa una categoria de ingreso o gasto
 * (por ejemplo Salario o Mercado). Corresponde a la tabla "categoria".
 */
public class Categoria {

    private Long idCategoria;
    private String nombre;
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
