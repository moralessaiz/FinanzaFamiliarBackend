package com.finanzafamiliar.contabilidad.controller;

import com.finanzafamiliar.contabilidad.model.Movimiento;
import com.finanzafamiliar.contabilidad.model.ResumenMovimientos;
import com.finanzafamiliar.contabilidad.service.MovimientoService;
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
 * Servicios REST del modulo de Movimientos (CRUD de ingresos y gastos).
 *
 * Reemplaza a ListarMovimientosServlet y RegistrarMovimientoServlet.
 *
 * | Metodo | Ruta                         | Antes (Servlets)                |
 * |--------|------------------------------|---------------------------------|
 * | GET    | /api/movimientos?tipo=       | GET  /movimientos?tipo=         |
 * | GET    | /api/movimientos/resumen     | (no existia)                    |
 * | GET    | /api/movimientos/{id}        | (no existia)                    |
 * | POST   | /api/movimientos             | POST /movimientos/registrar     |
 * | PUT    | /api/movimientos/{id}        | (solo en la prueba de consola)  |
 * | DELETE | /api/movimientos/{id}        | (solo en la prueba de consola)  |
 */
@RestController
@RequestMapping("/api/movimientos")
public class MovimientoController {

    private final MovimientoService movimientoService;

    public MovimientoController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    /** READ - Lista los movimientos; admite el filtro opcional ?tipo=ingreso, gasto o todos. */
    @GetMapping
    public List<Movimiento> listarMovimientos(@RequestParam(required = false) String tipo) {
        return movimientoService.listarMovimientos(tipo);
    }

    /** READ - Total de ingresos, total de gastos y saldo del hogar. */
    @GetMapping("/resumen")
    public ResumenMovimientos consultarResumen() {
        return movimientoService.consultarResumen();
    }

    /** READ - Consulta un movimiento por su id. */
    @GetMapping("/{id}")
    public Movimiento buscarMovimiento(@PathVariable Long id) {
        return movimientoService.buscarPorId(id);
    }

    /** CREATE - Registra un movimiento nuevo. Responde 201 (Created). */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Movimiento crearMovimiento(@Valid @RequestBody Movimiento movimiento) {
        return movimientoService.crearMovimiento(movimiento);
    }

    /** UPDATE - Actualiza un movimiento existente. */
    @PutMapping("/{id}")
    public Movimiento actualizarMovimiento(@PathVariable Long id, @Valid @RequestBody Movimiento movimiento) {
        return movimientoService.actualizarMovimiento(id, movimiento);
    }

    /** DELETE - Elimina un movimiento. Responde 204 (No Content). */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminarMovimiento(@PathVariable Long id) {
        movimientoService.eliminarMovimiento(id);
    }
}
