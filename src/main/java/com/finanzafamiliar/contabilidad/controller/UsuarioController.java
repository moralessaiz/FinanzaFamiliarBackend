package com.finanzafamiliar.contabilidad.controller;

import com.finanzafamiliar.contabilidad.config.OpenApiConfig;
import com.finanzafamiliar.contabilidad.model.RespuestaEliminacion;
import com.finanzafamiliar.contabilidad.model.RespuestaError;
import com.finanzafamiliar.contabilidad.model.Usuario;
import com.finanzafamiliar.contabilidad.service.UsuarioService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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
 * Las anotaciones @Operation, @Parameter y @ApiResponse documentan cada servicio
 * en Swagger UI (http://localhost:8080/swagger-ui.html).
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
@Tag(name = OpenApiConfig.TAG_USUARIOS)
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    /** READ - Lista los usuarios; admite el filtro opcional ?nombre=texto. */
    @Operation(summary = "Listar usuarios",
            description = "Devuelve los usuarios ordenados por nombre. Si se envía el parámetro `nombre`, "
                    + "devuelve solo los usuarios cuyo nombre contiene ese texto (sin importar mayúsculas).")
    @ApiResponse(responseCode = "200", description = "Lista de usuarios (puede estar vacía)")
    @GetMapping
    public List<Usuario> listarUsuarios(
            @Parameter(description = "Texto a buscar dentro del nombre (opcional)", example = "cris")
            @RequestParam(required = false) String nombre) {
        return usuarioService.listarUsuarios(nombre);
    }

    /** READ - Consulta un usuario por su id. */
    @Operation(summary = "Consultar un usuario", description = "Devuelve los datos de un usuario a partir de su id.")
    @ApiResponse(responseCode = "200", description = "Usuario encontrado")
    @ApiResponse(responseCode = "400", description = "El id no es un número",
            content = @Content(schema = @Schema(implementation = RespuestaError.class),
                    examples = @ExampleObject(value = EjemplosApi.ID_INVALIDO)))
    @ApiResponse(responseCode = "404", description = "No existe un usuario con ese id",
            content = @Content(schema = @Schema(implementation = RespuestaError.class),
                    examples = @ExampleObject(value = EjemplosApi.USUARIO_NO_ENCONTRADO)))
    @GetMapping("/{id}")
    public Usuario buscarUsuario(@Parameter(description = "Id del usuario", example = "1") @PathVariable Long id) {
        return usuarioService.buscarPorId(id);
    }

    /** CREATE - Registra un usuario nuevo. Responde 201 (Created). */
    @Operation(summary = "Registrar un usuario",
            description = "Crea un integrante del grupo familiar. El nombre se guarda sin espacios sobrantes "
                    + "y el correo en minúscula. No se envía `idUsuario`: lo asigna la base de datos.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = EjemplosApi.USUARIO_NUEVO)))
    @ApiResponse(responseCode = "201", description = "Usuario creado; devuelve el usuario con su id",
            content = @Content(schema = @Schema(implementation = Usuario.class),
                    examples = @ExampleObject(value = EjemplosApi.USUARIO_CREADO)))
    @ApiResponse(responseCode = "400", description = "Algún campo no cumple las validaciones",
            content = @Content(schema = @Schema(implementation = RespuestaError.class),
                    examples = @ExampleObject(value = EjemplosApi.USUARIO_DATOS_INVALIDOS)))
    @ApiResponse(responseCode = "409", description = "Ya existe un usuario con ese correo",
            content = @Content(schema = @Schema(implementation = RespuestaError.class),
                    examples = @ExampleObject(value = EjemplosApi.USUARIO_CORREO_REPETIDO)))
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Usuario crearUsuario(@Valid @RequestBody Usuario usuario) {
        return usuarioService.crearUsuario(usuario);
    }

    /** UPDATE - Actualiza un usuario existente. */
    @Operation(summary = "Actualizar un usuario",
            description = "Reemplaza el nombre, el correo y el rol de un usuario existente. Se envían todos los campos.")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            content = @Content(examples = @ExampleObject(value = EjemplosApi.USUARIO_NUEVO)))
    @ApiResponse(responseCode = "200", description = "Usuario actualizado; devuelve los datos nuevos")
    @ApiResponse(responseCode = "400", description = "Algún campo no cumple las validaciones",
            content = @Content(schema = @Schema(implementation = RespuestaError.class),
                    examples = @ExampleObject(value = EjemplosApi.USUARIO_DATOS_INVALIDOS)))
    @ApiResponse(responseCode = "404", description = "No existe un usuario con ese id",
            content = @Content(schema = @Schema(implementation = RespuestaError.class),
                    examples = @ExampleObject(value = EjemplosApi.USUARIO_NO_ENCONTRADO)))
    @ApiResponse(responseCode = "409", description = "El correo ya lo tiene otro usuario",
            content = @Content(schema = @Schema(implementation = RespuestaError.class),
                    examples = @ExampleObject(value = EjemplosApi.USUARIO_CORREO_REPETIDO)))
    @PutMapping("/{id}")
    public Usuario actualizarUsuario(@Parameter(description = "Id del usuario", example = "1") @PathVariable Long id,
                                     @Valid @RequestBody Usuario usuario) {
        return usuarioService.actualizarUsuario(id, usuario);
    }

    /** DELETE - Elimina un usuario. Responde 200 (OK) con un mensaje de confirmacion y los datos eliminados. */
    @Operation(summary = "Eliminar un usuario",
            description = "Elimina un usuario que no tenga movimientos registrados y devuelve un mensaje de "
                    + "confirmación con los datos que tenía.")
    @ApiResponse(responseCode = "200", description = "Usuario eliminado; devuelve un mensaje de confirmación y los datos que tenía")
    @ApiResponse(responseCode = "404", description = "No existe un usuario con ese id",
            content = @Content(schema = @Schema(implementation = RespuestaError.class),
                    examples = @ExampleObject(value = EjemplosApi.USUARIO_NO_ENCONTRADO)))
    @ApiResponse(responseCode = "409", description = "El usuario tiene movimientos registrados",
            content = @Content(schema = @Schema(implementation = RespuestaError.class),
                    examples = @ExampleObject(value = EjemplosApi.USUARIO_CON_MOVIMIENTOS)))
    @DeleteMapping("/{id}")
    public RespuestaEliminacion<Usuario> eliminarUsuario(
            @Parameter(description = "Id del usuario", example = "8") @PathVariable Long id) {
        Usuario eliminado = usuarioService.eliminarUsuario(id);
        return new RespuestaEliminacion<>(
                "Se eliminó el usuario " + eliminado.getNombre() + " (id " + id + ")", eliminado);
    }
}
