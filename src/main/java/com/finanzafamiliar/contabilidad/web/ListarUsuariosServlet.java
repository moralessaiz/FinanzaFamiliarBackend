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
import java.util.List;

/**
 * Servlet encargado de listar los integrantes del grupo familiar
 * (tabla "usuario") del Sistema de Contabilidad Familiar - FinanzaFamiliar.
 *
 * - doGet: lee el parametro opcional "nombre" de la cadena de consulta
 *   (por ejemplo: /usuarios?nombre=laura). Si viene, busca los usuarios
 *   cuyo nombre lo contenga; si no, consulta todos. El resultado se
 *   muestra en la vista JSP "usuarios/listar.jsp".
 *
 * Evidencia GA7-220501096-AA2-EV02 - Modulos de software codificados y probados.
 */
@WebServlet("/usuarios")
public class ListarUsuariosServlet extends HttpServlet {

    private static final String VISTA = "/WEB-INF/vistas/usuarios/listar.jsp";

    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String nombre = request.getParameter("nombre");

        try {
            List<Usuario> usuarios;
            if (nombre == null || nombre.trim().isEmpty()) {
                usuarios = usuarioDAO.consultarTodos();
            } else {
                usuarios = usuarioDAO.buscarPorNombre(nombre.trim());
            }
            request.setAttribute("usuarios", usuarios);

        } catch (SQLException e) {
            request.setAttribute("error", "No fue posible consultar los usuarios: " + e.getMessage());
        }

        request.getRequestDispatcher(VISTA).forward(request, response);
    }
}
