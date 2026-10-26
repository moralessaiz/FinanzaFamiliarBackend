package com.finanzafamiliar.contabilidad.repository;

import com.finanzafamiliar.contabilidad.model.Categoria;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio (antes CategoriaDAO) del catalogo de categorias (solo consulta).
 */
@Repository
public class CategoriaRepository {

    private static final String SQL_CONSULTAR_TODAS =
            "SELECT id_categoria, nombre, tipo FROM categoria ORDER BY tipo, nombre";

    private static final String SQL_CONSULTAR_POR_ID =
            "SELECT id_categoria, nombre, tipo FROM categoria WHERE id_categoria = ?";

    private final JdbcTemplate jdbcTemplate;

    private final RowMapper<Categoria> categoriaMapper = (resultado, numeroFila) -> new Categoria(
            resultado.getLong("id_categoria"),
            resultado.getString("nombre"),
            resultado.getString("tipo")
    );

    public CategoriaRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    /**
     * Consulta todas las categorias, ordenadas por tipo y nombre.
     */
    public List<Categoria> consultarTodas() {
        return jdbcTemplate.query(SQL_CONSULTAR_TODAS, categoriaMapper);
    }

    /**
     * Consulta una categoria por su identificador.
     */
    public Optional<Categoria> consultarPorId(Long idCategoria) {
        return jdbcTemplate.query(SQL_CONSULTAR_POR_ID, categoriaMapper, idCategoria).stream().findFirst();
    }
}
