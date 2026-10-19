package com.finanzafamiliar.contabilidad.web;

/**
 * Utilidad para las vistas JSP del Sistema de Contabilidad Familiar - FinanzaFamiliar.
 *
 * Escapa los caracteres especiales de HTML antes de imprimir en la pagina
 * un dato escrito por el usuario. Asi se evita que un texto como
 * "&lt;script&gt;" se ejecute en el navegador (ataque XSS).
 *
 * Evidencia GA7-220501096-AA2-EV02 - Modulos de software codificados y probados.
 */
public final class HtmlUtil {

    private HtmlUtil() {
    }

    /**
     * Convierte &amp;, &lt;, &gt;, comillas dobles y simples en entidades HTML.
     *
     * @return el texto seguro para imprimir, o una cadena vacia si es null.
     */
    public static String escapar(Object valor) {
        if (valor == null) {
            return "";
        }
        return valor.toString()
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
