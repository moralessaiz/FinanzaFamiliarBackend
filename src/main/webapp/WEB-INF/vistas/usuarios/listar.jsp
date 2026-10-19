<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.finanzafamiliar.contabilidad.modelo.Usuario" %>
<%@ page import="com.finanzafamiliar.contabilidad.web.HtmlUtil" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>FinanzaFamiliar - Usuarios</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/estilos.css">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/fragmentos/encabezado.jsp">
        <jsp:param name="activo" value="usuarios"/>
    </jsp:include>

    <main>
        <h2>Integrantes del grupo familiar</h2>

        <% if (request.getParameter("registrado") != null) { %>
            <p class="mensaje-exito">Usuario registrado correctamente (id <%= HtmlUtil.escapar(request.getParameter("registrado")) %>).</p>
        <% } %>

        <% if (request.getAttribute("error") != null) { %>
            <p class="mensaje-error"><%= HtmlUtil.escapar(request.getAttribute("error")) %></p>
        <% } %>

        <%--
            Formulario de busqueda enviado por metodo GET: el texto escrito
            viaja en la URL (por ejemplo /usuarios?nombre=laura) y lo lee
            ListarUsuariosServlet con request.getParameter("nombre").
            Por ser GET, la busqueda se puede guardar como favorito o compartir.
        --%>
        <form action="<%= request.getContextPath() %>/usuarios" method="get" class="formulario-filtro">
            <label for="nombre">Buscar por nombre:</label>
            <input type="text" id="nombre" name="nombre" value="<%= HtmlUtil.escapar(request.getParameter("nombre")) %>">
            <button type="submit">Buscar</button>
        </form>

        <table class="tabla-movimientos">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Nombre</th>
                    <th>Correo</th>
                    <th>Rol</th>
                </tr>
            </thead>
            <tbody>
                <%
                    List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");
                    if (usuarios != null && !usuarios.isEmpty()) {
                        for (Usuario u : usuarios) {
                %>
                        <tr>
                            <td><%= u.getIdUsuario() %></td>
                            <td><%= HtmlUtil.escapar(u.getNombre()) %></td>
                            <td><%= HtmlUtil.escapar(u.getCorreo()) %></td>
                            <td><%= HtmlUtil.escapar(u.getRol()) %></td>
                        </tr>
                <%
                        }
                    } else {
                %>
                        <tr>
                            <td colspan="4">No se encontraron usuarios.</td>
                        </tr>
                <%
                    }
                %>
            </tbody>
        </table>
    </main>

    <%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>
</body>
</html>
