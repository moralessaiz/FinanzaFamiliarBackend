package com.finanzafamiliar.contabilidad.modelo;

/**
 * Clase modelo (POJO) que representa una categoria de ingreso o gasto
 * del Sistema de Contabilidad Familiar - FinanzaFamiliar.
 *
 * Se agrega en esta evidencia para poder listar las categorias
 * disponibles en el formulario web de registro de movimientos.
 *
 * Evidencia GA7-220501096-AA2-EV02 - Modulos de software codificados y probados.
 */
public class Categoria {

    private int idCategoria;
    private String nombre;
    private String tipo;

    public Categoria() {
    }

    public Categoria(int idCategoria, String nombre, String tipo) {
        this.idCategoria = idCategoria;
        this.nombre = nombre;
        this.tipo = tipo;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
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
