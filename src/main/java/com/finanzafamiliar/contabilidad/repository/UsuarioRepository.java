package com.finanzafamiliar.contabilidad.repository;

import com.finanzafamiliar.contabilidad.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio (antes UsuarioDAO) de la tabla "usuario".
 *
 * Implementa el CRUD con JdbcTemplate, que es JDBC administrado por Spring:
 * el SQL y los parametros "?" son los mismos de la version con Servlets,
 * pero Spring se encarga de abrir y cerrar la conexion, el PreparedStatement
 * y el ResultSet (ya no hace falta el try-with-resources).
 */
@Repository
public class UsuarioRepository {

    private static final String SQL_CONSULTAR_TODOS =
            "SELECT id_usuario, nombre, correo, rol FROM usuario ORDER BY nombre";

    private static final String SQL_BUSCAR_POR_NOMBRE =
            "SELECT id_usuario, nombre, correo, rol FROM usuario WHERE nombre LIKE ? ORDER BY nombre";

    private static final String SQL_CONSULTAR_POR_ID =
            "SELECT id_usuario, nombre, correo, rol FROM usuario WHERE id_usuario = ?";

    private static final String SQL_CONTAR_POR_CORREO =
            "SELECT COUNT(*) FROM usuario WHERE correo = ? AND id_usuario <> ?";

    private static final String SQL_INSERTAR =
            "INSERT INTO usuario (nombre, correo, rol) VALUES (?, ?, ?)";

    private static final String SQL_ACTUALIZAR =
            "UPDATE usuario SET nombre = ?, correo = ?, rol = ? WHERE id_usuario = ?";

    private static final String SQL_ELIMINAR = "DELETE FROM usuario WHERE id_usuario = ?";

    private final JdbcTemplate jdbcTemplate;

    /** Convierte cada fila del ResultSet en un objeto Usuario (antes mapearUsuario). */
    private final RowMapper<Usuario> usuarioMapper = (resultado, numeroFila) -> new Usuario(
            resultado.getLong("id_usuario"),
            resultado.getString("nombre"),
            resultado.getString("correo"),
            resultado.getString("rol")
    );

    public UsuarioRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * READ - Consulta todos los usuarios registrados, ordenados por nombre.
     */
    public List<Usuario> consultarTodos() {
        return jdbcTemplate.query(SQL_CONSULTAR_TODOS, usuarioMapper);
    }

    /**
     * READ - Busca los usuarios cuyo nombre contenga el texto indicado.
     */
    public List<Usuario> buscarPorNombre(String texto) {
        return jdbcTemplate.query(SQL_BUSCAR_POR_NOMBRE, usuarioMapper, "%" + texto + "%");
    }

    /**
     * READ - Consulta un usuario por su identificador.
     *
     * @return un Optional con el usuario, o vacio si no existe.
     */
    public Optional<Usuario> consultarPorId(Long idUsuario) {
        return jdbcTemplate.query(SQL_CONSULTAR_POR_ID, usuarioMapper, idUsuario).stream().findFirst();
    }

    /**
     * Indica si ya existe otro usuario con el mismo correo.
     *
     * @param idExcluido id del usuario que se esta editando (0 si es un usuario nuevo),
     *                   para que no se compare consigo mismo.
     */
    public boolean existeCorreo(String correo, Long idExcluido) {
        Integer cantidad = jdbcTemplate.queryForObject(SQL_CONTAR_POR_CORREO, Integer.class, correo, idExcluido);
        return cantidad != null && cantidad > 0;
    }

    /**
     * CREATE - Inserta un nuevo usuario.
     *
     * @return el id autogenerado del usuario insertado.
     */
    public Long insertar(Usuario usuario) {
        KeyHolder llaves = new GeneratedKeyHolder();
        jdbcTemplate.update(conexion -> {
            PreparedStatement sentencia = conexion.prepareStatement(SQL_INSERTAR, Statement.RETURN_GENERATED_KEYS);
            sentencia.setString(1, usuario.getNombre());
            sentencia.setString(2, usuario.getCorreo());
            sentencia.setString(3, usuario.getRol());
            return sentencia;
        }, llaves);
        return llaves.getKey().longValue();
    }

    /**
     * UPDATE - Actualiza los datos de un usuario existente.
     *
     * @return true si se actualizo una fila, false si el id no existe.
     */
    public boolean actualizar(Long idUsuario, Usuario usuario) {
        int filas = jdbcTemplate.update(SQL_ACTUALIZAR,
                usuario.getNombre(),
                usuario.getCorreo(),
                usuario.getRol(),
                idUsuario);
        return filas > 0;
    }

    /**
     * DELETE - Elimina un usuario.
     *
     * @return true si se elimino una fila, false si el id no existe.
     */
    public boolean eliminar(Long idUsuario) {
        return jdbcTemplate.update(SQL_ELIMINAR, idUsuario) > 0;
    }
}
