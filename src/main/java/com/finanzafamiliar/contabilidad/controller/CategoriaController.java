package com.finanzafamiliar.contabilidad.controller;

import com.finanzafamiliar.contabilidad.model.Categoria;
import com.finanzafamiliar.contabilidad.repository.CategoriaRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Servicio REST para consultar las categorias. El front-end lo usa para llenar
 * la lista desplegable del formulario de movimientos.
 *
 * No tiene capa de servicio porque solo consulta y no aplica ninguna regla.
 */
@RestController
@RequestMapping("/api/categorias")
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    /** READ - Lista todas las categorias. */
    @GetMapping
    public List<Categoria> listarCategorias() {
        return categoriaRepository.consultarTodas();
    }
}
