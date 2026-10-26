package com.finanzafamiliar.contabilidad.controller;

import com.finanzafamiliar.contabilidad.exception.ConflictoException;
import com.finanzafamiliar.contabilidad.exception.RecursoNoEncontradoException;
import com.finanzafamiliar.contabilidad.model.Usuario;
import com.finanzafamiliar.contabilidad.service.UsuarioService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas del controlador de usuarios. @WebMvcTest levanta solo la capa web
 * (controlador + ManejadorExcepciones) y el servicio se reemplaza por un
 * simulacro (mock), asi que no se necesita MySQL para ejecutarlas.
 */
@WebMvcTest(UsuarioController.class)
class UsuarioControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UsuarioService usuarioService;

    @Test
    @DisplayName("GET /api/usuarios responde 200 con la lista en JSON")
    void listarUsuariosDevuelveJson() throws Exception {
        when(usuarioService.listarUsuarios(null)).thenReturn(List.of(
                new Usuario(1L, "Amalfi Aya", "caterine7226@gmail.com", "Administrador")));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].idUsuario").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Amalfi Aya"));
    }

    @Test
    @DisplayName("POST con datos validos responde 201 con el usuario creado")
    void crearUsuarioValido() throws Exception {
        when(usuarioService.crearUsuario(any(Usuario.class))).thenReturn(
                new Usuario(3L, "Laura Morales", "laura@correo.com", "Integrante"));

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Laura Morales", "correo": "laura@correo.com", "rol": "Integrante"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idUsuario").value(3));
    }

    @Test
    @DisplayName("POST con campos vacios responde 400 con un mensaje por campo")
    void crearUsuarioVacioDevuelve400() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "", "correo": "", "rol": ""}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("Datos inválidos"))
                .andExpect(jsonPath("$.errores.nombre").exists())
                .andExpect(jsonPath("$.errores.correo").exists())
                .andExpect(jsonPath("$.errores.rol").exists());

        verify(usuarioService, never()).crearUsuario(any());
    }

    @Test
    @DisplayName("POST con nombre con numeros, correo invalido y rol desconocido responde 400")
    void crearUsuarioConFormatosInvalidos() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "Laura123<script>", "correo": "laura.com", "rol": "Jefe"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").value("El nombre solo puede contener letras y espacios"))
                .andExpect(jsonPath("$.errores.correo").value("El correo electrónico no es válido"))
                .andExpect(jsonPath("$.errores.rol").value("El rol debe ser Administrador o Integrante"));
    }

    @Test
    @DisplayName("POST con un nombre de mas de 80 caracteres responde 400")
    void crearUsuarioConNombreMuyLargo() throws Exception {
        String nombreLargo = "a".repeat(81);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nombre\": \"" + nombreLargo + "\", \"correo\": \"a@b.co\", \"rol\": \"Integrante\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.nombre").value("El nombre no puede superar 80 caracteres"));
    }

    @Test
    @DisplayName("GET de un usuario que no existe responde 404")
    void buscarUsuarioInexistente() throws Exception {
        when(usuarioService.buscarPorId(99L))
                .thenThrow(new RecursoNoEncontradoException("No se encontró el usuario con id 99"));

        mockMvc.perform(get("/api/usuarios/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("No se encontró el usuario con id 99"));
    }

    @Test
    @DisplayName("GET con un id que no es numero responde 400")
    void buscarUsuarioConIdNoNumerico() throws Exception {
        mockMvc.perform(get("/api/usuarios/abc"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El parámetro 'id' no tiene un valor válido"));
    }

    @Test
    @DisplayName("DELETE de un usuario con movimientos responde 409")
    void eliminarUsuarioConMovimientos() throws Exception {
        doThrow(new ConflictoException("No se puede eliminar a Amalfi Aya porque tiene 2 movimiento(s)"))
                .when(usuarioService).eliminarUsuario(1L);

        mockMvc.perform(delete("/api/usuarios/1"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.mensaje").exists());
    }

    @Test
    @DisplayName("DELETE de un usuario sin movimientos responde 204")
    void eliminarUsuario() throws Exception {
        mockMvc.perform(delete("/api/usuarios/5"))
                .andExpect(status().isNoContent());

        verify(usuarioService).eliminarUsuario(5L);
    }
}
