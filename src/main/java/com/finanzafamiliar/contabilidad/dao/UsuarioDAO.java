package com.finanzafamiliar.contabilidad.dao;

import com.finanzafamiliar.contabilidad.conexion.ConexionBD;
import com.finanzafamiliar.contabilidad.modelo.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos (DAO) para el modulo de Usuarios del
 * Sistema de Contabilidad Familiar - FinanzaFamiliar.
 *
 * Implementa las cuatro operaciones del CRUD (Crear, Consultar,
 * Actualizar y Eliminar) sobre la tabla "usuario", utilizando JDBC
 * con sentencias preparadas (PreparedStatement) para evitar inyeccion SQL.
 *
 * Evidencia GA7-220501096-AA2-EV01 - Codificacion de modulos del software.
 */
public class UsuarioDAO {

    /**
     * CREATE - Inserta un nuevo usuario en la base de datos.
     *
     * @return el id autogenerado del usuario insertado.
     */
    public int insertar(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuario (nombre, correo, rol) VALUES (?, ?, ?)";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, usuario.getNombre());
            sentencia.setString(2, usuario.getCorreo());
            sentencia.setString(3, usuario.getRol());

            sentencia.executeUpdate();

            int idGenerado = 0;
            try (ResultSet llaves = sentencia.getGeneratedKeys()) {
                if (llaves.next()) {
                    idGenerado = llaves.getInt(1);
                }
            }
            return idGenerado;
        }
    }

    /**
     * READ - Consulta todos los usuarios registrados, ordenados por nombre.
     */
    public List<Usuario> consultarTodos() throws SQLException {
        String sql = "SELECT id_usuario, nombre, correo, rol FROM usuario ORDER BY nombre";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                usuarios.add(mapearUsuario(resultado));
            }
        }
        return usuarios;
    }

    /**
     * READ - Busca los usuarios cuyo nombre contenga el texto indicado.
     *
     * Se agrega en la evidencia GA7-220501096-AA2-EV02 para atender el
     * buscador por parametro GET ("nombre") del modulo web de usuarios
     * (ListarUsuariosServlet).
     */
    public List<Usuario> buscarPorNombre(String texto) throws SQLException {
        String sql = "SELECT id_usuario, nombre, correo, rol FROM usuario WHERE nombre LIKE ? ORDER BY nombre";
        List<Usuario> usuarios = new ArrayList<>();

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, "%" + texto + "%");
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    usuarios.add(mapearUsuario(resultado));
                }
            }
        }
        return usuarios;
    }

    /**
     * READ - Consulta un usuario especifico por su identificador.
     *
     * @return el usuario encontrado, o null si no existe.
     */
    public Usuario consultarPorId(int idUsuario) throws SQLException {
        String sql = "SELECT id_usuario, nombre, correo, rol FROM usuario WHERE id_usuario = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idUsuario);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapearUsuario(resultado);
                }
            }
        }
        return null;
    }

    /**
     * UPDATE - Actualiza los datos de un usuario existente.
     *
     * @return true si el usuario fue actualizado, false si no existia.
     */
    public boolean actualizar(Usuario usuario) throws SQLException {
        String sql = "UPDATE usuario SET nombre = ?, correo = ?, rol = ? WHERE id_usuario = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, usuario.getNombre());
            sentencia.setString(2, usuario.getCorreo());
            sentencia.setString(3, usuario.getRol());
            sentencia.setInt(4, usuario.getIdUsuario());

            int filasAfectadas = sentencia.executeUpdate();
            return filasAfectadas > 0;
        }
    }

    /**
     * DELETE - Elimina un usuario de la base de datos a partir de su identificador.
     * Si el usuario tiene movimientos registrados, MySQL rechaza la eliminacion
     * por la llave foranea fk_movimiento_usuario y se lanza una SQLException.
     *
     * @return true si el usuario fue eliminado, false si no existia.
     */
    public boolean eliminar(int idUsuario) throws SQLException {
        String sql = "DELETE FROM usuario WHERE id_usuario = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idUsuario);
            int filasAfectadas = sentencia.executeUpdate();
            return filasAfectadas > 0;
        }
    }

    /**
     * Convierte la fila actual de un ResultSet en un objeto Usuario.
     */
    private Usuario mapearUsuario(ResultSet resultado) throws SQLException {
        return new Usuario(
                resultado.getInt("id_usuario"),
                resultado.getString("nombre"),
                resultado.getString("correo"),
                resultado.getString("rol")
        );
    }
}
