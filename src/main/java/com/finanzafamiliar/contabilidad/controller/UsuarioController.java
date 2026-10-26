package com.finanzafamiliar.contabilidad.controller;

import com.finanzafamiliar.contabilidad.model.Usuario;
import com.finanzafamiliar.contabilidad.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Servicios REST del modulo de Usuarios (integrantes del grupo familiar).
 *
 * Reemplaza a ListarUsuariosServlet y RegistrarUsuarioServlet: en lugar de
 * reenviar a una JSP, cada metodo devuelve el objeto y Spring lo convierte a JSON.
 *
 * | Metodo | Ruta                  | Antes (Servlets)                  |
 * |--------|-----------------------|-----------------------------------|
 * | GET    | /api/usuarios?nombre= | GET  /usuarios?nombre=            |
 * | GET    | /api/usuarios/{id}    | (no existia)                      |
 * | POST   | /api/usuarios         | POST /usuarios/registrar          |
 * | PUT    | /api/usuarios/{id}    | (solo en la prueba de consola)    |
 * | DELETE | /api/usuarios/{id}    | (solo en la prueba de consola)    |
 */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** READ - Lista los usuarios; admite el filtro opcional ?nombre=texto. */
    @GetMapping
    public List<Usuario> listarUsuarios(@RequestParam(required = false) String nombre) {
        return usuarioService.listarUsuarios(nombre);
    }

    /** READ - Consulta un usuario por su id. */
    @GetMapping("/{id}")
    public Usuario buscarUsuario(@PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    /** CREATE - Registra un usuario nuevo. Responde 201 (Created). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario crearUsuario(@Valid @RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(usuario);
    }

    /** UPDATE - Actualiza un usuario existente. */
    @PutMapping("/{id}")
    public Usuario actualizarUsuario(@PathVariable Long id, @Valid @RequestBody Usuario usuario) {
        return usuarioService.actualizarUsuario(id, usuario);
    }

    /** DELETE - Elimina un usuario. Responde 204 (No Content). */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarUsuario(@PathVariable Long id) {
        usuarioService.eliminarUsuario(id);
    }
}
