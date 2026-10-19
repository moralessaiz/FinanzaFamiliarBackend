package com.finanzafamiliar.contabilidad.web;

import com.finanzafamiliar.contabilidad.dao.UsuarioDAO;
import com.finanzafamiliar.contabilidad.modelo.Usuario;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * Servlet encargado del registro de integrantes del grupo familiar
 * (tabla "usuario") del Sistema de Contabilidad Familiar - FinanzaFamiliar,
 * a traves de un formulario HTML embebido en la vista JSP "usuarios/registrar.jsp".
 *
 * - doGet: muestra el formulario vacio.
 * - doPost: recibe nombre, correo y rol, los valida y registra el usuario
 *   en la base de datos por medio de UsuarioDAO.
 *
 * Evidencia GA7-220501096-AA2-EV02 - Modulos de software codificados y probados.
 */
@WebServlet("/usuarios/registrar")
public class RegistrarUsuarioServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/vistas/usuarios/registrar.jsp";

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher(VISTA).forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");

        try {
            Usuario usuario = leerFormulario(request);
            int idGenerado = usuarioDAO.insertar(usuario);
            // Patron POST-Redirect-GET: evita registrar dos veces si se recarga la pagina
            response.sendRedirect(request.getContextPath() + "/usuarios?registrado=" + idGenerado);

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Datos invalidos en el formulario: " + e.getMessage());
            request.getRequestDispatcher(VISTA).forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "No fue posible registrar el usuario: " + e.getMessage());
            request.getRequestDispatcher(VISTA).forward(request, response);
        }
    }

    /**
     * Lee y valida los parametros del formulario y construye el Usuario.
     *
     * @throws IllegalArgumentException si falta un campo o el correo no es valido.
     */
    private Usuario leerFormulario(HttpServletRequest request) {
        String nombre = request.getParameter("nombre");
        String correo = request.getParameter("correo");
        String rol = request.getParameter("rol");

        if (nombre == null || nombre.trim().isEmpty() || correo == null || correo.trim().isEmpty()
                || rol == null || rol.isEmpty()) {
            throw new IllegalArgumentException("Todos los campos del formulario son obligatorios.");
        }
        if (!correo.contains("@")) {
            throw new IllegalArgumentException("El correo electronico no es valido.");
        }

        return new Usuario(nombre.trim(), correo.trim(), rol);
    }
}
