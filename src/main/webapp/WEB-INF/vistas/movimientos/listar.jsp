<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.finanzafamiliar.contabilidad.modelo.Movimiento" %>
<%@ page import="com.finanzafamiliar.contabilidad.web.HtmlUtil" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>FinanzaFamiliar - Movimientos</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/estilos.css">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/fragmentos/encabezado.jsp">
        <jsp:param name="activo" value="movimientos"/>
    </jsp:include>

    <main>
        <h2>Movimientos registrados</h2>

        <% if (request.getParameter("registrado") != null) { %>
            <p class="mensaje-exito">Movimiento registrado correctamente (id <%= HtmlUtil.escapar(request.getParameter("registrado")) %>).</p>
        <% } %>

        <% if (request.getAttribute("error") != null) { %>
            <p class="mensaje-error"><%= HtmlUtil.escapar(request.getAttribute("error")) %></p>
        <% } %>

        <%
            String tipoSeleccionado = (String) request.getAttribute("tipoSeleccionado");
            if (tipoSeleccionado == null) {
                tipoSeleccionado = "todos";
            }
        %>

        <%--
            Formulario de filtro enviado por metodo GET: el parametro
            "tipo" queda visible en la URL (por ejemplo /movimientos?tipo=ingreso)
            y es leido por ListarMovimientosServlet con request.getParameter("tipo").
        --%>
        <form action="<%= request.getContextPath() %>/movimientos" method="get" class="formulario-filtro">
            <label for="tipo">Filtrar por tipo:</label>
            <select id="tipo" name="tipo" onchange="this.form.submit()">
                <option value="todos" <%= "todos".equals(tipoSeleccionado) ? "selected" : "" %>>Todos</option>
                <option value="ingreso" <%= "ingreso".equals(tipoSeleccionado) ? "selected" : "" %>>Ingresos</option>
                <option value="gasto" <%= "gasto".equals(tipoSeleccionado) ? "selected" : "" %>>Gastos</option>
            </select>
            <noscript><button type="submit">Filtrar</button></noscript>
        </form>

        <table class="tabla-movimientos">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Tipo</th>
                    <th>Monto</th>
                    <th>Concepto</th>
                    <th>Fecha</th>
                    <th>Categoria</th>
                    <th>Usuario</th>
                </tr>
            </thead>
            <tbody>
                <%
                    List<Movimiento> movimientos = (List<Movimiento>) request.getAttribute("movimientos");
                    if (movimientos != null && !movimientos.isEmpty()) {
                        for (Movimiento m : movimientos) {
                %>
                        <tr class="<%= "ingreso".equals(m.getTipo()) ? "fila-ingreso" : "fila-gasto" %>">
                            <td><%= m.getIdMovimiento() %></td>
                            <td><%= HtmlUtil.escapar(m.getTipo()) %></td>
                            <td><%= m.getMonto() %></td>
                            <td><%= HtmlUtil.escapar(m.getConcepto()) %></td>
                            <td><%= m.getFecha() %></td>
                            <td><%= m.getIdCategoria() %></td>
                            <td><%= m.getIdUsuario() %></td>
                        </tr>
                <%
                        }
                    } else {
                %>
                        <tr>
                            <td colspan="7">No hay movimientos registrados para este filtro.</td>
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
