<%--
    Fragmento dinamico: encabezado y menu de navegacion de todas las paginas.
    Se incluye con la accion <jsp:include>, que recibe el parametro "activo"
    para resaltar la opcion del menu en la que se encuentra el usuario.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
    String activo = request.getParameter("activo");
    String raiz = request.getContextPath();
%>
<header>
    <h1>FinanzaFamiliar</h1>
    <p>Sistema de Contabilidad Familiar</p>
</header>

<nav>
    <a href="<%= raiz %>/" class="<%= "inicio".equals(activo) ? "activo" : "" %>">Inicio</a>
    <a href="<%= raiz %>/usuarios" class="<%= "usuarios".equals(activo) ? "activo" : "" %>">Usuarios</a>
    <a href="<%= raiz %>/usuarios/registrar" class="<%= "registrarUsuario".equals(activo) ? "activo" : "" %>">Registrar usuario</a>
    <a href="<%= raiz %>/movimientos" class="<%= "movimientos".equals(activo) ? "activo" : "" %>">Movimientos</a>
    <a href="<%= raiz %>/movimientos/registrar" class="<%= "registrarMovimiento".equals(activo) ? "activo" : "" %>">Registrar movimiento</a>
</nav>
