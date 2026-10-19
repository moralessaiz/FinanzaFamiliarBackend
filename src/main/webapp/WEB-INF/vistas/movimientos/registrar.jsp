<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.finanzafamiliar.contabilidad.modelo.Categoria" %>
<%@ page import="com.finanzafamiliar.contabilidad.modelo.Usuario" %>
<%@ page import="com.finanzafamiliar.contabilidad.web.HtmlUtil" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>FinanzaFamiliar - Registrar movimiento</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/estilos.css">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/fragmentos/encabezado.jsp">
        <jsp:param name="activo" value="registrarMovimiento"/>
    </jsp:include>

    <main>
        <h2>Registrar ingreso o gasto</h2>

        <% if (request.getAttribute("error") != null) { %>
            <p class="mensaje-error"><%= HtmlUtil.escapar(request.getAttribute("error")) %></p>
        <% } %>

        <%--
            Formulario HTML con servlet: se envia por metodo POST al
            servlet RegistrarMovimientoServlet (mapeado en /movimientos/registrar),
            que procesa los parametros y registra el movimiento en la
            base de datos. Con POST los datos viajan en el cuerpo de la
            peticion y no quedan visibles en la URL.
        --%>
        <form action="<%= request.getContextPath() %>/movimientos/registrar" method="post" class="formulario">

            <label for="tipo">Tipo de movimiento</label>
            <select id="tipo" name="tipo" required>
                <option value="ingreso">Ingreso</option>
                <option value="gasto">Gasto</option>
            </select>

            <label for="monto">Monto (COP)</label>
            <input type="number" id="monto" name="monto" step="0.01" min="0" required
                   value="<%= HtmlUtil.escapar(request.getParameter("monto")) %>">

            <label for="concepto">Concepto</label>
            <input type="text" id="concepto" name="concepto" maxlength="150" required
                   value="<%= HtmlUtil.escapar(request.getParameter("concepto")) %>">

            <label for="fecha">Fecha</label>
            <input type="date" id="fecha" name="fecha" required
                   value="<%= HtmlUtil.escapar(request.getParameter("fecha")) %>">

            <label for="idCategoria">Categoria</label>
            <select id="idCategoria" name="idCategoria" required>
                <%
                    List<Categoria> categorias = (List<Categoria>) request.getAttribute("categorias");
                    if (categorias != null) {
                        for (Categoria categoria : categorias) {
                %>
                            <option value="<%= categoria.getIdCategoria() %>">
                                <%= HtmlUtil.escapar(categoria.getNombre()) %> (<%= HtmlUtil.escapar(categoria.getTipo()) %>)
                            </option>
                <%
                        }
                    }
                %>
            </select>

            <label for="idUsuario">Registrado por</label>
            <select id="idUsuario" name="idUsuario" required>
                <%
                    List<Usuario> usuarios = (List<Usuario>) request.getAttribute("usuarios");
                    if (usuarios != null) {
                        for (Usuario usuario : usuarios) {
                %>
                            <option value="<%= usuario.getIdUsuario() %>"><%= HtmlUtil.escapar(usuario.getNombre()) %></option>
                <%
                        }
                    }
                %>
            </select>

            <button type="submit">Guardar movimiento</button>
        </form>
    </main>

    <%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>
</body>
</html>
