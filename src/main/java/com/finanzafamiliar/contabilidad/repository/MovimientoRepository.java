package com.finanzafamiliar.contabilidad.repository;

import com.finanzafamiliar.contabilidad.model.Movimiento;
import com.finanzafamiliar.contabilidad.model.ResumenMovimientos;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio (antes MovimientoDAO) de la tabla "movimiento".
 *
 * Las consultas hacen JOIN con "categoria" y "usuario" para devolver tambien
 * el nombre de la categoria y el de quien registro el movimiento.
 */
@Repository
public class MovimientoRepository {

    private static final String SQL_CONSULTA_BASE =
            "SELECT m.id_movimiento, m.tipo, m.monto, m.concepto, m.fecha, m.id_categoria, m.id_usuario, "
            + "c.nombre AS nombre_categoria, u.nombre AS nombre_usuario "
            + "FROM movimiento m "
            + "INNER JOIN categoria c ON c.id_categoria = m.id_categoria "
            + "INNER JOIN usuario u ON u.id_usuario = m.id_usuario ";

    private static final String SQL_ORDEN = "ORDER BY m.fecha DESC, m.id_movimiento DESC";

    private static final String SQL_INSERTAR =
            "INSERT INTO movimiento (tipo, monto, concepto, fecha, id_categoria, id_usuario) VALUES (?, ?, ?, ?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE movimiento SET tipo = ?, monto = ?, concepto = ?, fecha = ?, id_categoria = ?, id_usuario = ? "
            + "WHERE id_movimiento = ?";

    private static final String SQL_ELIMINAR = "DELETE FROM movimiento WHERE id_movimiento = ?";

    private static final String SQL_CONTAR_POR_USUARIO = "SELECT COUNT(*) FROM movimiento WHERE id_usuario = ?";

    private static final String SQL_RESUMEN =
            "SELECT COALESCE(SUM(CASE WHEN tipo = 'ingreso' THEN monto END), 0) AS total_ingresos, "
            + "COALESCE(SUM(CASE WHEN tipo = 'gasto' THEN monto END), 0) AS total_gastos, "
            + "COUNT(*) AS cantidad "
            + "FROM movimiento";

    private final JdbcTemplate jdbcTemplate;

    /** Convierte cada fila del ResultSet en un objeto Movimiento. */
    private final RowMapper<Movimiento> movimientoMapper = (resultado, numeroFila) -> {
        Movimiento movimiento = new Movimiento();
        movimiento.setIdMovimiento(resultado.getLong("id_movimiento"));
        movimiento.setTipo(resultado.getString("tipo"));
        movimiento.setMonto(resultado.getBigDecimal("monto"));
        movimiento.setConcepto(resultado.getString("concepto"));
        movimiento.setFecha(resultado.getObject("fecha", LocalDate.class));
        movimiento.setIdCategoria(resultado.getLong("id_categoria"));
        movimiento.setIdUsuario(resultado.getLong("id_usuario"));
        movimiento.setNombreCategoria(resultado.getString("nombre_categoria"));
        movimiento.setNombreUsuario(resultado.getString("nombre_usuario"));
        return movimiento;
    };

    public MovimientoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * READ - Consulta todos los movimientos, del mas reciente al mas antiguo.
     */
    public List<Movimiento> consultarTodos() {
        return jdbcTemplate.query(SQL_CONSULTA_BASE + SQL_ORDEN, movimientoMapper);
    }

    /**
     * READ - Consulta los movimientos de un tipo (ingreso o gasto).
     */
    public List<Movimiento> consultarPorTipo(String tipo) {
        return jdbcTemplate.query(SQL_CONSULTA_BASE + "WHERE m.tipo = ? " + SQL_ORDEN, movimientoMapper, tipo);
    }

    /**
     * READ - Consulta un movimiento por su identificador.
     *
     * @return un Optional con el movimiento, o vacio si no existe.
     */
    public Optional<Movimiento> consultarPorId(Long idMovimiento) {
        return jdbcTemplate.query(SQL_CONSULTA_BASE + "WHERE m.id_movimiento = ?", movimientoMapper, idMovimiento)
                .stream()
                .findFirst();
    }

    /**
     * Cuenta cuantos movimientos ha registrado un usuario. Se usa para no
     * intentar eliminar un usuario que tiene movimientos (llave foranea).
     */
    public int contarPorUsuario(Long idUsuario) {
        Integer cantidad = jdbcTemplate.queryForObject(SQL_CONTAR_POR_USUARIO, Integer.class, idUsuario);
        return cantidad == null ? 0 : cantidad;
    }

    /**
     * Calcula el total de ingresos, el total de gastos y el saldo del hogar.
     */
    public ResumenMovimientos consultarResumen() {
        return jdbcTemplate.queryForObject(SQL_RESUMEN, (resultado, numeroFila) -> {
            BigDecimal ingresos = resultado.getBigDecimal("total_ingresos");
            BigDecimal gastos = resultado.getBigDecimal("total_gastos");
            return new ResumenMovimientos(ingresos, gastos, ingresos.subtract(gastos), resultado.getLong("cantidad"));
        });
    }

    /**
     * CREATE - Inserta un nuevo movimiento.
     *
     * @return el id autogenerado del movimiento insertado.
     */
    public Long insertar(Movimiento movimiento) {
        KeyHolder llaves = new GeneratedKeyHolder();
        jdbcTemplate.update(conexion -> {
            PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS);
            sentencia.setString(1, movimiento.getTipo());
            sentencia.setBigDecimal(2, movimiento.getMonto());
            sentencia.setString(3, movimiento.getConcepto());
            sentencia.setDate(4, Date.valueOf(movimiento.getFecha()));
            sentencia.setLong(5, movimiento.getIdCategoria());
            sentencia.setLong(6, movimiento.getIdUsuario());
            return sentencia;
        }, llaves);
        return llaves.getKey().longValue();
    }

    /**
     * UPDATE - Actualiza los datos de un movimiento existente.
     *
     * @return true si se actualizo una fila, false si el id no existe.
     */
    public boolean actualizar(Long idMovimiento, Movimiento movimiento) {
        int filas = jdbcTemplate.update(SQL_ACTUALIZAR,
                movimiento.getTipo(),
                movimiento.getMonto(),
                movimiento.getConcepto(),
                Date.valueOf(movimiento.getFecha()),
                movimiento.getIdCategoria(),
                movimiento.getIdUsuario(),
                idMovimiento);
        return filas > 0;
    }

    /**
     * DELETE - Elimina un movimiento.
     *
     * @return true si se elimino una fila, false si el id no existe.
     */
    public boolean eliminar(Long idMovimiento) {
        return jdbcTemplate.update(SQL_ELIMINAR, idMovimiento) > 0;
    }
}
