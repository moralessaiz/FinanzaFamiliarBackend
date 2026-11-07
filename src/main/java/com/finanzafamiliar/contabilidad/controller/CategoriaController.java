package com.finanzafamiliar.contabilidad.controller;

import com.finanzafamiliar.contabilidad.config.OpenApiConfig;
import com.finanzafamiliar.contabilidad.model.Categoria;
import com.finanzafamiliar.contabilidad.repository.CategoriaRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = OpenApiConfig.TAG_CATEGORIAS)
public class CategoriaController {

    private final CategoriaRepository categoriaRepository;

    public CategoriaController(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    /** READ - Lista todas las categorias. */
    @Operation(summary = "Listar categorías",
            description = "Devuelve las categorías de ingreso y gasto, ordenadas por tipo y nombre. "
                    + "Su `idCategoria` es el que se envía al registrar un movimiento.")
    @ApiResponse(responseCode = "200", description = "Lista de categorías")
    @GetMapping
    public List<Categoria> listarCategorias() {
        return categoriaRepository.consultarTodas();
    }
}
