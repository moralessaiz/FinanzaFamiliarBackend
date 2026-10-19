<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.finanzafamiliar.contabilidad.web.HtmlUtil" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>FinanzaFamiliar - Registrar usuario</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/estilos.css">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/fragmentos/encabezado.jsp">
        <jsp:param name="activo" value="registrarUsuario"/>
    </jsp:include>

    <main>
        <h2>Registrar integrante del grupo familiar</h2>

        <% if (request.getAttribute("error") != null) { %>
            <p class="mensaje-error"><%= HtmlUtil.escapar(request.getAttribute("error")) %></p>
        <% } %>

        <%
            // Si el registro fallo, se vuelven a mostrar los datos que el usuario ya habia escrito
            String rolElegido = request.getParameter("rol");
        %>

        <%--
            Formulario HTML con servlet: se envia por metodo POST al servlet
            RegistrarUsuarioServlet (mapeado en /usuarios/registrar), que valida
            los datos y los guarda en la tabla "usuario" por medio de UsuarioDAO.
        --%>
        <form action="<%= request.getContextPath() %>/usuarios/registrar" method="post" class="formulario">

            <label for="nombre">Nombre completo</label>
            <input type="text" id="nombre" name="nombre" maxlength="80" required
                   value="<%= HtmlUtil.escapar(request.getParameter("nombre")) %>">

            <label for="correo">Correo electronico</label>
            <input type="email" id="correo" name="correo" maxlength="100" required
                   value="<%= HtmlUtil.escapar(request.getParameter("correo")) %>">

            <label for="rol">Rol</label>
            <select id="rol" name="rol" required>
                <option value="Integrante" <%= "Integrante".equals(rolElegido) ? "selected" : "" %>>Integrante</option>
                <option value="Administrador" <%= "Administrador".equals(rolElegido) ? "selected" : "" %>>Administrador</option>
            </select>

            <button type="submit">Guardar usuario</button>
        </form>
    </main>

    <%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>
</body>
</html>
