package com.finanzafamiliar.contabilidad.web;

import com.finanzafamiliar.contabilidad.dao.CategoriaDAO;
import com.finanzafamiliar.contabilidad.dao.MovimientoDAO;
import com.finanzafamiliar.contabilidad.dao.UsuarioDAO;
import com.finanzafamiliar.contabilidad.modelo.Movimiento;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;

/**
 * Servlet encargado del registro de movimientos (ingresos y gastos) del
 * Sistema de Contabilidad Familiar - FinanzaFamiliar, a traves de un
 * formulario HTML embebido en la vista JSP "movimientos/registrar.jsp".
 *
 * - doGet: carga las categorias y los usuarios y muestra el formulario vacio.
 * - doPost: recibe los parametros enviados por el formulario, valida y
 *   registra el movimiento en la base de datos por medio de MovimientoDAO.
 *
 * Basado en el modulo web de Amalfi Aya (proyecto_ModuloMovimientos_Web).
 * Cambio: la lista "Registrado por" se carga desde la tabla usuario.
 *
 * Evidencia GA7-220501096-AA2-EV02 - Modulos de software codificados y probados.
 */
@WebServlet("/movimientos/registrar")
public class RegistrarMovimientoServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/vistas/movimientos/registrar.jsp";

    private final MovimientoDAO movimientoDAO = new MovimientoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        cargarListasYReenviar(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            Movimiento movimiento = leerFormulario(request);
            int idGenerado = movimientoDAO.insertar(movimiento);
            // Patron POST-Redirect-GET: evita registrar dos veces si se recarga la pagina
            response.sendRedirect(request.getContextPath() + "/movimientos?registrado=" + idGenerado);

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Datos invalidos en el formulario: " + e.getMessage());
            cargarListasYReenviar(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "No fue posible registrar el movimiento: " + e.getMessage());
            cargarListasYReenviar(request, response);
        }
    }

    /**
     * Lee y valida los parametros del formulario y construye el Movimiento.
     *
     * @throws IllegalArgumentException si falta un campo o un valor no tiene el formato esperado.
     */
    private Movimiento leerFormulario(HttpServletRequest request) {
        String tipo = request.getParameter("tipo");
        String montoTexto = request.getParameter("monto");
        String concepto = request.getParameter("concepto");
        String fechaTexto = request.getParameter("fecha");

        if (!"ingreso".equals(tipo) && !"gasto".equals(tipo)) {
            throw new IllegalArgumentException("El tipo debe ser ingreso o gasto.");
        }
        if (montoTexto == null || montoTexto.isEmpty() || concepto == null || concepto.trim().isEmpty()
                || fechaTexto == null || fechaTexto.isEmpty()) {
            throw new IllegalArgumentException("Todos los campos del formulario son obligatorios.");
        }

        return new Movimiento(
                tipo,
                new BigDecimal(montoTexto),
                concepto.trim(),
                Date.valueOf(fechaTexto),
                Integer.parseInt(request.getParameter("idCategoria")),
                Integer.parseInt(request.getParameter("idUsuario"))
        );
    }

    /**
     * Carga las categorias y los usuarios para las listas desplegables y muestra el formulario.
     */
    private void cargarListasYReenviar(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            request.setAttribute("categorias", categoriaDAO.consultarTodas());
            request.setAttribute("usuarios", usuarioDAO.consultarTodos());
        } catch (SQLException e) {
            if (request.getAttribute("error") == null) {
                request.setAttribute("error", "No fue posible cargar las listas del formulario: " + e.getMessage());
            }
        }
        request.getRequestDispatcher(VISTA).forward(request, response);
    }
}
