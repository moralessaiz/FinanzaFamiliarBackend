<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.time.LocalTime" %>
<%!
    // Declaracion JSP: define un metodo que queda disponible en toda la pagina.
    private String saludoSegunHora() {
        int hora = LocalTime.now().getHour();
        if (hora < 12) {
            return "Buenos dias";
        } else if (hora < 19) {
            return "Buenas tardes";
        }
        return "Buenas noches";
    }
%>
<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <title>FinanzaFamiliar - Inicio</title>
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/estilos.css">
</head>
<body>
    <jsp:include page="/WEB-INF/vistas/fragmentos/encabezado.jsp">
        <jsp:param name="activo" value="inicio"/>
    </jsp:include>

    <main>
        <h2><%= saludoSegunHora() %>, bienvenido a FinanzaFamiliar</h2>
        <p>Seleccione el modulo con el que desea trabajar:</p>

        <div class="tarjetas">
            <a class="tarjeta" href="<%= request.getContextPath() %>/usuarios">
                <h3>Usuarios</h3>
                <p>Registrar y consultar los integrantes del grupo familiar.</p>
            </a>
            <a class="tarjeta" href="<%= request.getContextPath() %>/movimientos">
                <h3>Movimientos</h3>
                <p>Registrar y consultar los ingresos y gastos del hogar.</p>
            </a>
        </div>
    </main>

    <%@ include file="/WEB-INF/vistas/fragmentos/pie.jspf" %>
</body>
</html>
