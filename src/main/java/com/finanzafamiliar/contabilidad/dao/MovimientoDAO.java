package com.finanzafamiliar.contabilidad.dao;

import com.finanzafamiliar.contabilidad.conexion.ConexionBD;
import com.finanzafamiliar.contabilidad.modelo.Movimiento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de acceso a datos (DAO) para el modulo de Movimientos del
 * Sistema de Contabilidad Familiar - FinanzaFamiliar.
 *
 * Implementa las cuatro operaciones del CRUD (Crear, Consultar,
 * Actualizar y Eliminar) sobre la tabla "movimiento", utilizando JDBC
 * con sentencias preparadas (PreparedStatement) para evitar inyeccion SQL.
 *
 * Evidencia GA7-220501096-AA2-EV01 - Codificacion de modulos del software.
 */
public class MovimientoDAO {

    /**
     * CREATE - Inserta un nuevo movimiento (ingreso o gasto) en la base de datos.
     *
     * @return el id autogenerado del movimiento insertado.
     */
    public int insertar(Movimiento movimiento) throws SQLException {
        String sql = "INSERT INTO movimiento (tipo, monto, concepto, fecha, id_categoria, id_usuario) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {

            sentencia.setString(1, movimiento.getTipo());
            sentencia.setBigDecimal(2, movimiento.getMonto());
            sentencia.setString(3, movimiento.getConcepto());
            sentencia.setDate(4, movimiento.getFecha());
            sentencia.setInt(5, movimiento.getIdCategoria());
            sentencia.setInt(6, movimiento.getIdUsuario());

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
     * READ - Consulta todos los movimientos registrados, ordenados por fecha.
     */
    public List<Movimiento> consultarTodos() throws SQLException {
        String sql = "SELECT id_movimiento, tipo, monto, concepto, fecha, id_categoria, id_usuario "
                + "FROM movimiento ORDER BY fecha DESC, id_movimiento DESC";
        List<Movimiento> movimientos = new ArrayList<>();

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql);
             ResultSet resultado = sentencia.executeQuery()) {

            while (resultado.next()) {
                movimientos.add(mapearMovimiento(resultado));
            }
        }
        return movimientos;
    }

    /**
     * READ - Consulta los movimientos filtrados por tipo (ingreso o gasto).
     *
     * Se agrega en la evidencia GA7-220501096-AA2-EV02 para atender el
     * filtro por parametro GET ("tipo") del modulo web de listado de
     * movimientos (ListarMovimientosServlet).
     */
    public List<Movimiento> consultarPorTipo(String tipo) throws SQLException {
        String sql = "SELECT id_movimiento, tipo, monto, concepto, fecha, id_categoria, id_usuario "
                + "FROM movimiento WHERE tipo = ? ORDER BY fecha DESC, id_movimiento DESC";
        List<Movimiento> movimientos = new ArrayList<>();

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, tipo);
            try (ResultSet resultado = sentencia.executeQuery()) {
                while (resultado.next()) {
                    movimientos.add(mapearMovimiento(resultado));
                }
            }
        }
        return movimientos;
    }

    /**
     * READ - Consulta un movimiento especifico por su identificador.
     */
    public Movimiento consultarPorId(int idMovimiento) throws SQLException {
        String sql = "SELECT id_movimiento, tipo, monto, concepto, fecha, id_categoria, id_usuario "
                + "FROM movimiento WHERE id_movimiento = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idMovimiento);
            try (ResultSet resultado = sentencia.executeQuery()) {
                if (resultado.next()) {
                    return mapearMovimiento(resultado);
                }
            }
        }
        return null;
    }

    /**
     * UPDATE - Actualiza los datos de un movimiento existente.
     *
     * @return true si el movimiento fue actualizado, false si no existia.
     */
    public boolean actualizar(Movimiento movimiento) throws SQLException {
        String sql = "UPDATE movimiento SET tipo = ?, monto = ?, concepto = ?, fecha = ?, "
                + "id_categoria = ?, id_usuario = ? WHERE id_movimiento = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setString(1, movimiento.getTipo());
            sentencia.setBigDecimal(2, movimiento.getMonto());
            sentencia.setString(3, movimiento.getConcepto());
            sentencia.setDate(4, movimiento.getFecha());
            sentencia.setInt(5, movimiento.getIdCategoria());
            sentencia.setInt(6, movimiento.getIdUsuario());
            sentencia.setInt(7, movimiento.getIdMovimiento());

            int filasAfectadas = sentencia.executeUpdate();
            return filasAfectadas > 0;
        }
    }

    /**
     * DELETE - Elimina un movimiento de la base de datos a partir de su identificador.
     *
     * @return true si el movimiento fue eliminado, false si no existia.
     */
    public boolean eliminar(int idMovimiento) throws SQLException {
        String sql = "DELETE FROM movimiento WHERE id_movimiento = ?";

        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement sentencia = conexion.prepareStatement(sql)) {

            sentencia.setInt(1, idMovimiento);
            int filasAfectadas = sentencia.executeUpdate();
            return filasAfectadas > 0;
        }
    }

    /**
     * Convierte la fila actual de un ResultSet en un objeto Movimiento.
     */
    private Movimiento mapearMovimiento(ResultSet resultado) throws SQLException {
        return new Movimiento(
                resultado.getInt("id_movimiento"),
                resultado.getString("tipo"),
                resultado.getBigDecimal("monto"),
                resultado.getString("concepto"),
                resultado.getDate("fecha"),
                resultado.getInt("id_categoria"),
                resultado.getInt("id_usuario")
        );
    }
}
