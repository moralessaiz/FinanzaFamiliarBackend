package com.finanzafamiliar.contabilidad.service;

import com.finanzafamiliar.contabilidad.exception.ConflictoException;
import com.finanzafamiliar.contabilidad.exception.RecursoNoEncontradoException;
import com.finanzafamiliar.contabilidad.model.Usuario;
import com.finanzafamiliar.contabilidad.repository.MovimientoRepository;
import com.finanzafamiliar.contabilidad.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;

/**
 * Logica de negocio del modulo de Usuarios.
 *
 * Esta capa no existia en la version con Servlets: alli el servlet validaba
 * y llamaba directamente al DAO. Separarla permite que el controlador solo
 * se ocupe de HTTP y que las reglas queden en un unico lugar.
 */
@Service
public class UsuarioService {

    /** Id que nunca existe, para no excluir a nadie al revisar correos repetidos de un usuario nuevo. */
    private static final long SIN_EXCLUIR = 0L;

    private final UsuarioRepository usuarioRepository;
    private final MovimientoRepository movimientoRepository;

    public UsuarioService(UsuarioRepository usuarioRepository, MovimientoRepository movimientoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.movimientoRepository = movimientoRepository;
    }

    /**
     * Lista todos los usuarios, o solo los que contengan el texto en su nombre.
     */
    public List<Usuario> listarUsuarios(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return usuarioRepository.consultarTodos();
        }
        return usuarioRepository.buscarPorNombre(nombre.trim());
    }

    /**
     * Busca un usuario por id.
     *
     * @throws RecursoNoEncontradoException si no existe (404).
     */
    public Usuario buscarPorId(Long idUsuario) {
        return usuarioRepository.consultarPorId(idUsuario)
                .orElseThrow(() -> new RecursoNoEncontradoException("No se encontró el usuario con id " + idUsuario));
    }

    /**
     * Registra un usuario nuevo y lo devuelve con el id generado.
     *
     * @throws ConflictoException si ya existe un usuario con el mismo correo (409).
     */
    public Usuario crearUsuario(Usuario usuario) {
        normalizar(usuario);
        validarCorreoUnico(usuario.getCorreo(), SIN_EXCLUIR);
        Long idNuevo = usuarioRepository.insertar(usuario);
        return buscarPorId(idNuevo);
    }

    /**
     * Actualiza un usuario existente.
     *
     * @throws RecursoNoEncontradoException si el usuario no existe (404).
     * @throws ConflictoException si el correo ya lo tiene otro usuario (409).
     */
    public Usuario actualizarUsuario(Long idUsuario, Usuario usuario) {
        buscarPorId(idUsuario);
        normalizar(usuario);
        validarCorreoUnico(usuario.getCorreo(), idUsuario);
        usuarioRepository.actualizar(idUsuario, usuario);
        return buscarPorId(idUsuario);
    }

    /**
     * Elimina un usuario que no tenga movimientos registrados.
     *
     * @throws RecursoNoEncontradoException si el usuario no existe (404).
     * @throws ConflictoException si el usuario tiene movimientos (409). Asi se da un
     *                            mensaje claro en lugar del error de llave foranea de MySQL.
     */
    public void eliminarUsuario(Long idUsuario) {
        Usuario usuario = buscarPorId(idUsuario);
        int cantidadMovimientos = movimientoRepository.contarPorUsuario(idUsuario);
        if (cantidadMovimientos > 0) {
            throw new ConflictoException("No se puede eliminar a " + usuario.getNombre() + " porque tiene "
                    + cantidadMovimientos + " movimiento(s) registrado(s). Elimine primero sus movimientos.");
        }
        usuarioRepository.eliminar(idUsuario);
    }

    /**
     * Quita espacios sobrantes del nombre y guarda el correo en minuscula,
     * para que "  Laura   Morales " y "Laura Morales" se guarden igual.
     */
    private void normalizar(Usuario usuario) {
        usuario.setNombre(usuario.getNombre().trim().replaceAll("\\s+", " "));
        usuario.setCorreo(usuario.getCorreo().trim().toLowerCase(Locale.ROOT));
    }

    private void validarCorreoUnico(String correo, Long idExcluido) {
        if (usuarioRepository.existeCorreo(correo, idExcluido)) {
            throw new ConflictoException("Ya existe un usuario registrado con el correo " + correo);
        }
    }
}
