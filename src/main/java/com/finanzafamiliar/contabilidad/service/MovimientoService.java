package com.finanzafamiliar.contabilidad.service;

import com.finanzafamiliar.contabilidad.exception.RecursoNoEncontradoException;
import com.finanzafamiliar.contabilidad.exception.ReglaNegocioException;
import com.finanzafamiliar.contabilidad.model.Categoria;
import com.finanzafamiliar.contabilidad.model.Movimiento;
import com.finanzafamiliar.contabilidad.model.ResumenMovimientos;
import com.finanzafamiliar.contabilidad.repository.CategoriaRepository;
import com.finanzafamiliar.contabilidad.repository.MovimientoRepository;
import com.finanzafamiliar.contabilidad.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Logica de negocio del modulo de Movimientos (ingresos y gastos).
 */
@Service
public class MovimientoService {

    private static final String TIPO_INGRESO = "ingreso";
    private static final String TIPO_GASTO = "gasto";
    private static final String FILTRO_TODOS = "todos";

    private final MovimientoRepository movimientoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;

    public MovimientoService(MovimientoRepository movimientoRepository,
                             CategoriaRepository categoriaRepository,
                             UsuarioRepository usuarioRepository) {
        this.movimientoRepository = movimientoRepository;
        this.categoriaRepository = categoriaRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /**
     * Lista los movimientos. El filtro tipo es opcional: ingreso, gasto o todos
     * (igual que el parametro GET de la version con Servlets).
     *
     * @throws ReglaNegocioException si el filtro tiene otro valor (400).
     */
    public List<Movimiento> listarMovimientos(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            return movimientoRepository.consultarTodos();
        }
        String tipoNormalizado = tipo.trim().toLowerCase(Locale.ROOT);
        if (tipoNormalizado.equals(FILTRO_TODOS)) {
            return movimientoRepository.consultarTodos();
        }
        if (!tipoNormalizado.equals(TIPO_INGRESO) && !tipoNormalizado.equals(TIPO_GASTO)) {
            throw new ReglaNegocioException("El filtro tipo debe ser ingreso, gasto o todos");
        }
        return movimientoRepository.consultarPorTipo(tipoNormalizado);
    }

    /**
     * Busca un movimiento por id.
     *
     * @throws RecursoNoEncontradoException si no existe (404).
     */
    public Movimiento buscarPorId(Long idMovimiento) {
        return movimientoRepository.consultarPorId(idMovimiento)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No se encontró el movimiento con id " + idMovimiento));
    }

    /**
     * Devuelve el total de ingresos, el total de gastos y el saldo del hogar.
     */
    public ResumenMovimientos consultarResumen() {
        return movimientoRepository.consultarResumen();
    }

    /**
     * Registra un movimiento nuevo y lo devuelve con su id y los nombres de categoria y usuario.
     */
    public Movimiento crearMovimiento(Movimiento movimiento) {
        validarReferencias(movimiento);
        movimiento.setConcepto(movimiento.getConcepto().trim());
        Long idNuevo = movimientoRepository.insertar(movimiento);
        return buscarPorId(idNuevo);
    }

    /**
     * Actualiza un movimiento existente.
     *
     * @throws RecursoNoEncontradoException si el movimiento no existe (404).
     */
    public Movimiento actualizarMovimiento(Long idMovimiento, Movimiento movimiento) {
        buscarPorId(idMovimiento);
        validarReferencias(movimiento);
        movimiento.setConcepto(movimiento.getConcepto().trim());
        movimientoRepository.actualizar(idMovimiento, movimiento);
        return buscarPorId(idMovimiento);
    }

    /**
     * Elimina un movimiento.
     *
     * @throws RecursoNoEncontradoException si el movimiento no existe (404).
     */
    public void eliminarMovimiento(Long idMovimiento) {
        if (!movimientoRepository.eliminar(idMovimiento)) {
            throw new RecursoNoEncontradoException("No se encontró el movimiento con id " + idMovimiento);
        }
    }

    /**
     * Revisa que la categoria y el usuario existan, y que la categoria sea del
     * mismo tipo del movimiento (por ejemplo, "Salario" solo sirve para ingresos).
     *
     * @throws ReglaNegocioException si alguna de las reglas no se cumple (400).
     */
    private void validarReferencias(Movimiento movimiento) {
        Categoria categoria = categoriaRepository.consultarPorId(movimiento.getIdCategoria())
                .orElseThrow(() -> new ReglaNegocioException(
                        "La categoría con id " + movimiento.getIdCategoria() + " no existe"));

        if (!categoria.getTipo().equals(movimiento.getTipo())) {
            throw new ReglaNegocioException("La categoría " + categoria.getNombre() + " es de tipo "
                    + categoria.getTipo() + " y no se puede usar en un " + movimiento.getTipo());
        }
        if (usuarioRepository.consultarPorId(movimiento.getIdUsuario()).isEmpty()) {
            throw new ReglaNegocioException("El usuario con id " + movimiento.getIdUsuario() + " no existe");
        }
    }
}
