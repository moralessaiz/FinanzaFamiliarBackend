package com.finanzafamiliar.contabilidad.web;

import com.finanzafamiliar.contabilidad.dao.MovimientoDAO;
import com.finanzafamiliar.contabilidad.modelo.Movimiento;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * Servlet encargado de listar los movimientos (ingresos y gastos) del
 * Sistema de Contabilidad Familiar - FinanzaFamiliar, con la posibilidad
 * de filtrar por tipo mediante un parametro enviado por metodo GET
 * (por ejemplo: /movimientos?tipo=ingreso).
 *
 * - doGet: lee el parametro opcional "tipo" de la cadena de consulta y
 *   consulta los movimientos correspondientes (todos, o solo ingresos,
 *   o solo gastos) para mostrarlos en la vista JSP "movimientos/listar.jsp".
 *
 * Basado en el modulo web de Amalfi Aya (proyecto_ModuloMovimientos_Web).
 *
 * Evidencia GA7-220501096-AA2-EV02 - Modulos de software codificados y probados.
 */
@WebServlet("/movimientos")
public class ListarMovimientosServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/vistas/movimientos/listar.jsp";

    private final MovimientoDAO movimientoDAO = new MovimientoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String tipo = request.getParameter("tipo");

        try {
            List<Movimiento> movimientos;
            if (tipo == null || tipo.isEmpty() || tipo.equals("todos")) {
                movimientos = movimientoDAO.consultarTodos();
                tipo = "todos";
            } else {
                movimientos = movimientoDAO.consultarPorTipo(tipo);
            }
            request.setAttribute("movimientos", movimientos);
            request.setAttribute("tipoSeleccionado", tipo);

        } catch (SQLException e) {
            request.setAttribute("error", "No fue posible consultar los movimientos: " + e.getMessage());
        }

        RequestDispatcher despachador = request.getRequestDispatcher(VISTA);
        despachador.forward(request, response);
    }
}
