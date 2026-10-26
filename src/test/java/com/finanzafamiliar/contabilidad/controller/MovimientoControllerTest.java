package com.finanzafamiliar.contabilidad.controller;

import com.finanzafamiliar.contabilidad.exception.ReglaNegocioException;
import com.finanzafamiliar.contabilidad.model.Movimiento;
import com.finanzafamiliar.contabilidad.model.ResumenMovimientos;
import com.finanzafamiliar.contabilidad.service.MovimientoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Pruebas del controlador de movimientos, centradas en las validaciones de
 * los datos que llegan desde el front-end (numeros, fechas, textos y longitudes).
 */
@WebMvcTest(MovimientoController.class)
class MovimientoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private MovimientoService movimientoService;

    @Test
    @DisplayName("POST con datos validos responde 201")
    void crearMovimientoValido() throws Exception {
        Movimiento creado = new Movimiento();
        creado.setIdMovimiento(10L);
        creado.setConcepto("Mercado semanal");
        when(movimientoService.crearMovimiento(any(Movimiento.class))).thenReturn(creado);

        mockMvc.perform(post("/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo": "gasto", "monto": 45000.50, "concepto": "Mercado semanal",
                                 "fecha": "2026-10-12", "idCategoria": 3, "idUsuario": 1}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.idMovimiento").value(10));
    }

    @Test
    @DisplayName("POST sin datos responde 400 con un error por cada campo obligatorio")
    void crearMovimientoVacio() throws Exception {
        mockMvc.perform(post("/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.tipo").exists())
                .andExpect(jsonPath("$.errores.monto").exists())
                .andExpect(jsonPath("$.errores.concepto").exists())
                .andExpect(jsonPath("$.errores.fecha").exists())
                .andExpect(jsonPath("$.errores.idCategoria").exists())
                .andExpect(jsonPath("$.errores.idUsuario").exists());

        verify(movimientoService, never()).crearMovimiento(any());
    }

    @Test
    @DisplayName("POST con monto negativo, tipo invalido y fecha futura responde 400")
    void crearMovimientoConValoresFueraDeRango() throws Exception {
        String manana = LocalDate.now().plusDays(1).toString();

        mockMvc.perform(post("/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\": \"prestamo\", \"monto\": -100, \"concepto\": \"Prueba\", "
                                + "\"fecha\": \"" + manana + "\", \"idCategoria\": 1, \"idUsuario\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.tipo").value("El tipo debe ser ingreso o gasto"))
                .andExpect(jsonPath("$.errores.monto").value("El monto debe ser mayor que cero"))
                .andExpect(jsonPath("$.errores.fecha").value("La fecha no puede ser posterior a hoy"));
    }

    @Test
    @DisplayName("POST con monto de mas de 2 decimales responde 400")
    void crearMovimientoConDemasiadosDecimales() throws Exception {
        mockMvc.perform(post("/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo": "gasto", "monto": 100.555, "concepto": "Prueba",
                                 "fecha": "2026-10-01", "idCategoria": 3, "idUsuario": 1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.monto").value("El monto admite máximo 10 dígitos enteros y 2 decimales"));
    }

    @Test
    @DisplayName("POST con concepto de mas de 150 caracteres responde 400")
    void crearMovimientoConConceptoMuyLargo() throws Exception {
        String conceptoLargo = "x".repeat(151);

        mockMvc.perform(post("/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\": \"gasto\", \"monto\": 100, \"concepto\": \"" + conceptoLargo + "\", "
                                + "\"fecha\": \"2026-10-01\", \"idCategoria\": 3, \"idUsuario\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.concepto").value("El concepto no puede superar 150 caracteres"));
    }

    @Test
    @DisplayName("POST con una fecha que no existe (31 de febrero) responde 400")
    void crearMovimientoConFechaInexistente() throws Exception {
        mockMvc.perform(post("/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo": "gasto", "monto": 100, "concepto": "Prueba",
                                 "fecha": "2026-02-31", "idCategoria": 3, "idUsuario": 1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.fecha").value("El valor no tiene un formato válido"));
    }

    @Test
    @DisplayName("POST con texto en el monto responde 400")
    void crearMovimientoConMontoNoNumerico() throws Exception {
        mockMvc.perform(post("/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo": "gasto", "monto": "cien mil", "concepto": "Prueba",
                                 "fecha": "2026-10-01", "idCategoria": 3, "idUsuario": 1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errores.monto").value("El valor no tiene un formato válido"));
    }

    @Test
    @DisplayName("POST con un JSON mal escrito responde 400")
    void crearMovimientoConJsonInvalido() throws Exception {
        mockMvc.perform(post("/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"tipo\": \"gasto\", "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje").value("El cuerpo de la petición no es un JSON válido"));
    }

    @Test
    @DisplayName("Una regla de negocio incumplida (categoria de otro tipo) responde 400 con su mensaje")
    void crearMovimientoConCategoriaDeOtroTipo() throws Exception {
        when(movimientoService.crearMovimiento(any(Movimiento.class))).thenThrow(new ReglaNegocioException(
                "La categoría Salario es de tipo ingreso y no se puede usar en un gasto"));

        mockMvc.perform(post("/api/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"tipo": "gasto", "monto": 100, "concepto": "Prueba",
                                 "fecha": "2026-10-01", "idCategoria": 1, "idUsuario": 1}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.mensaje")
                        .value("La categoría Salario es de tipo ingreso y no se puede usar en un gasto"));
    }

    @Test
    @DisplayName("GET /api/movimientos/resumen responde los totales")
    void consultarResumen() throws Exception {
        when(movimientoService.consultarResumen()).thenReturn(new ResumenMovimientos(
                new BigDecimal("2500000.00"), new BigDecimal("900000.00"), new BigDecimal("1600000.00"), 3));

        mockMvc.perform(get("/api/movimientos/resumen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalIngresos").value(2500000.00))
                .andExpect(jsonPath("$.saldo").value(1600000.00))
                .andExpect(jsonPath("$.cantidadMovimientos").value(3));
    }

    @Test
    @DisplayName("Una ruta que no existe responde 404 en formato JSON")
    void rutaInexistente() throws Exception {
        mockMvc.perform(get("/api/movimientos/1/otra-cosa"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.mensaje").value("La ruta solicitada no existe"));
    }
}
