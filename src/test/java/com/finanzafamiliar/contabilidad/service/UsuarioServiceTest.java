package com.finanzafamiliar.contabilidad.service;

import com.finanzafamiliar.contabilidad.exception.ConflictoException;
import com.finanzafamiliar.contabilidad.exception.RecursoNoEncontradoException;
import com.finanzafamiliar.contabilidad.model.Usuario;
import com.finanzafamiliar.contabilidad.repository.MovimientoRepository;
import com.finanzafamiliar.contabilidad.repository.UsuarioRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Pruebas de las reglas de negocio del modulo de usuarios. Los repositorios
 * se simulan con Mockito, por lo que no se toca la base de datos.
 */
@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private MovimientoRepository movimientoRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Al crear, quita espacios sobrantes del nombre y guarda el correo en minuscula")
    void crearNormalizaLosDatos() {
        Usuario nuevo = new Usuario(null, "  Laura    Morales ", " Laura.M@Correo.COM ", "Integrante");
        when(usuarioRepository.existeCorreo("laura.m@correo.com", 0L)).thenReturn(false);
        when(usuarioRepository.insertar(any(Usuario.class))).thenReturn(7L);
        when(usuarioRepository.consultarPorId(7L)).thenReturn(Optional.of(nuevo));

        usuarioService.crearUsuario(nuevo);

        ArgumentCaptor<Usuario> guardado = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).insertar(guardado.capture());
        assertThat(guardado.getValue().getNombre()).isEqualTo("Laura Morales");
        assertThat(guardado.getValue().getCorreo()).isEqualTo("laura.m@correo.com");
    }

    @Test
    @DisplayName("No permite registrar dos usuarios con el mismo correo")
    void crearConCorreoRepetido() {
        Usuario nuevo = new Usuario(null, "Otra Persona", "moralessaiz@gmail.com", "Integrante");
        when(usuarioRepository.existeCorreo("moralessaiz@gmail.com", 0L)).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.crearUsuario(nuevo))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("moralessaiz@gmail.com");
        verify(usuarioRepository, never()).insertar(any());
    }

    @Test
    @DisplayName("Al editar, no se compara el correo con el mismo usuario")
    void actualizarConservandoElCorreo() {
        Usuario existente = new Usuario(2L, "Cristhian Morales", "moralessaiz@gmail.com", "Administrador");
        when(usuarioRepository.consultarPorId(2L)).thenReturn(Optional.of(existente));
        when(usuarioRepository.existeCorreo("moralessaiz@gmail.com", 2L)).thenReturn(false);

        usuarioService.actualizarUsuario(2L, existente);

        verify(usuarioRepository).actualizar(2L, existente);
    }

    @Test
    @DisplayName("No permite eliminar un usuario que tiene movimientos")
    void eliminarUsuarioConMovimientos() {
        when(usuarioRepository.consultarPorId(1L)).thenReturn(Optional.of(
                new Usuario(1L, "Amalfi Aya", "caterine7226@gmail.com", "Administrador")));
        when(movimientoRepository.contarPorUsuario(1L)).thenReturn(3);

        assertThatThrownBy(() -> usuarioService.eliminarUsuario(1L))
                .isInstanceOf(ConflictoException.class)
                .hasMessageContaining("tiene 3 movimiento(s)");
        verify(usuarioRepository, never()).eliminar(anyLong());
    }

    @Test
    @DisplayName("Eliminar un usuario que no existe lanza 404")
    void eliminarUsuarioInexistente() {
        when(usuarioRepository.consultarPorId(50L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.eliminarUsuario(50L))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }

    @Test
    @DisplayName("Sin texto de busqueda devuelve todos; con texto, busca por nombre sin espacios sobrantes")
    void listarConYSinFiltro() {
        usuarioService.listarUsuarios("   ");
        verify(usuarioRepository).consultarTodos();

        usuarioService.listarUsuarios("  cris ");
        verify(usuarioRepository).buscarPorNombre("cris");
    }
}
