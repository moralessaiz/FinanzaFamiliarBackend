package com.finanzafamiliar.contabilidad.service;

import com.finanzafamiliar.contabilidad.exception.RecursoNoEncontradoException;
import com.finanzafamiliar.contabilidad.exception.ReglaNegocioException;
import com.finanzafamiliar.contabilidad.model.Categoria;
import com.finanzafamiliar.contabilidad.model.Movimiento;
import com.finanzafamiliar.contabilidad.model.Usuario;
import com.finanzafamiliar.contabilidad.repository.CategoriaRepository;
import com.finanzafamiliar.contabilidad.repository.MovimientoRepository;
import com.finanzafamiliar.contabilidad.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas de las reglas de negocio del modulo de movimientos.
 */
@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock
    private MovimientoRepository movimientoRepository;

    @Mock
    private CategoriaRepository categoriaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private MovimientoService movimientoService;

    @Test
    @DisplayName("No permite usar una categoria de ingreso en un gasto")
    void categoriaDeOtroTipo() {
        Movimiento gasto = crearMovimiento("gasto", 1L);
        when(categoriaRepository.consultarPorId(1L)).thenReturn(Optional.of(new Categoria(1L, "Salario", "ingreso")));

        assertThatThrownBy(() -> movimientoService.crearMovimiento(gasto))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessage("La categoría Salario es de tipo ingreso y no se puede usar en un gasto");
        verify(movimientoRepository, never()).insertar(any());
    }

    @Test
    @DisplayName("No permite usar una categoria que no existe")
    void categoriaInexistente() {
        Movimiento gasto = crearMovimiento("gasto", 99L);
        when(categoriaRepository.consultarPorId(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movimientoService.crearMovimiento(gasto))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no existe");
    }

    @Test
    @DisplayName("No permite registrar un movimiento a nombre de un usuario que no existe")
    void usuarioInexistente() {
        Movimiento gasto = crearMovimiento("gasto", 3L);
        gasto.setIdUsuario(40L);
        when(categoriaRepository.consultarPorId(3L)).thenReturn(Optional.of(new Categoria(3L, "Mercado", "gasto")));
        when(usuarioRepository.consultarPorId(40L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> movimientoService.crearMovimiento(gasto))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessage("El usuario con id 40 no existe");
    }

    @Test
    @DisplayName("Registra el movimiento valido con el concepto sin espacios sobrantes")
    void crearMovimientoValido() {
        Movimiento gasto = crearMovimiento("gasto", 3L);
        gasto.setConcepto("   Mercado del mes  ");
        when(categoriaRepository.consultarPorId(3L)).thenReturn(Optional.of(new Categoria(3L, "Mercado", "gasto")));
        when(usuarioRepository.consultarPorId(1L)).thenReturn(Optional.of(new Usuario()));
        when(movimientoRepository.insertar(gasto)).thenReturn(15L);
        when(movimientoRepository.consultarPorId(15L)).thenReturn(Optional.of(gasto));

        Movimiento creado = movimientoService.crearMovimiento(gasto);

        assertThat(creado.getConcepto()).isEqualTo("Mercado del mes");
    }

    @Test
    @DisplayName("El filtro por tipo acepta mayusculas y rechaza valores desconocidos")
    void filtroPorTipo() {
        movimientoService.listarMovimientos("GASTO");
        verify(movimientoRepository).consultarPorTipo("gasto");

        movimientoService.listarMovimientos("todos");
        verify(movimientoRepository).consultarTodos();

        assertThatThrownBy(() -> movimientoService.listarMovimientos("ahorro"))
                .isInstanceOf(ReglaNegocioException.class);
    }

    @Test
    @DisplayName("Eliminar un movimiento que no existe lanza 404")
    void eliminarInexistente() {
        when(movimientoRepository.eliminar(77L)).thenReturn(false);

        assertThatThrownBy(() -> movimientoService.eliminarMovimiento(77L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    private Movimiento crearMovimiento(String tipo, Long idCategoria) {
        Movimiento movimiento = new Movimiento();
        movimiento.setTipo(tipo);
        movimiento.setMonto(new BigDecimal("50000.00"));
        movimiento.setConcepto("Prueba");
        movimiento.setFecha(LocalDate.of(2026, 10, 1));
        movimiento.setIdCategoria(idCategoria);
        movimiento.setIdUsuario(1L);
        return movimiento;
    }
}
