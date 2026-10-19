package com.finanzafamiliar.contabilidad.dao;

import com.finanzafamiliar.contabilidad.conexion.ConexionBD;
import com.finanzafamiliar.contabilidad.modelo.Categoria;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos (DAO) para el catalogo de categorias del
 * Sistema de Contabilidad Familiar - FinanzaFamiliar.
 *
 * Se utiliza desde el modulo web para poblar la lista desplegable de
 * categorias del formulario de registro de movimientos.
 *
 * Evidencia GA7-220501096-AA2-EV02 - Modulos de software codificados y probados.
 */
public class CategoriaDAO {

    /**
     * Consulta todas las categorias registradas, ordenadas por tipo y nombre.
     */
    public List<Categoria> consultarTodas() throws SQLException {
        String sql = "SELECT id_categoria, nombre, tipo FROM categoria ORDER BY tipo, nombre";
        List<Categoria> categorias = new ArrayList<>();

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                categorias.add(new Categoria(
                        resultado.getInt("id_categoria"),
                        resultado.getString("nombre"),
                        resultado.getString("tipo")
                ));
            }
        }
        return categorias;
    }
}
