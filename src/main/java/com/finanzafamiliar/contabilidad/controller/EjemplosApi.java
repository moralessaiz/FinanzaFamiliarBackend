package com.finanzafamiliar.contabilidad.controller;

/**
 * Ejemplos de JSON que se muestran en la documentacion de Swagger para cada
 * servicio. Son los mismos mensajes que devuelve la API (ver los servicios y
 * ManejadorExcepciones), para que la documentacion coincida con la realidad.
 *
 * Se agrupan aqui para que las anotaciones de los controladores sean cortas.
 */
final class EjemplosApi {

    private EjemplosApi() {
    }

    // ----- Usuarios -----

    static final String USUARIO_NUEVO = """
            { "nombre": "Laura Morales", "correo": "laura.morales@correo.com", "rol": "Integrante" }""";

    static final String USUARIO_CREADO = """
            { "idUsuario": 8, "nombre": "Laura Morales", "correo": "laura.morales@correo.com", "rol": "Integrante" }""";

    static final String USUARIO_DATOS_INVALIDOS = """
            { "mensaje": "Datos inválidos",
              "errores": { "nombre": "El nombre solo puede contener letras y espacios",
                           "correo": "El correo electrónico no es válido",
                           "rol": "El rol debe ser Administrador o Integrante" } }""";

    static final String USUARIO_NO_ENCONTRADO = """
            { "mensaje": "No se encontró el usuario con id 99" }""";

    static final String USUARIO_CORREO_REPETIDO = """
            { "mensaje": "Ya existe un usuario registrado con el correo laura.morales@correo.com" }""";

    static final String USUARIO_CON_MOVIMIENTOS = """
            { "mensaje": "No se puede eliminar a Amalfi Aya porque tiene 3 movimiento(s) registrado(s). \
            Elimine primero sus movimientos." }""";

    // ----- Movimientos -----

    static final String MOVIMIENTO_NUEVO = """
            { "tipo": "gasto", "monto": 450000.00, "concepto": "Mercado del mes",
              "fecha": "2026-11-01", "idCategoria": 3, "idUsuario": 1 }""";

    static final String MOVIMIENTO_CREADO = """
            { "idMovimiento": 12, "tipo": "gasto", "monto": 450000.00, "concepto": "Mercado del mes",
              "fecha": "2026-11-01", "idCategoria": 3, "idUsuario": 1,
              "nombreCategoria": "Mercado", "nombreUsuario": "Amalfi Aya" }""";

    static final String MOVIMIENTO_DATOS_INVALIDOS = """
            { "mensaje": "Datos inválidos",
              "errores": { "monto": "El monto debe ser mayor que cero",
                           "fecha": "La fecha no puede ser posterior a hoy" } }""";

    static final String MOVIMIENTO_CATEGORIA_OTRO_TIPO = """
            { "mensaje": "La categoría Mercado es de tipo gasto y no se puede usar en un ingreso" }""";

    static final String MOVIMIENTO_FILTRO_INVALIDO = """
            { "mensaje": "El filtro tipo debe ser ingreso, gasto o todos" }""";

    static final String MOVIMIENTO_NO_ENCONTRADO = """
            { "mensaje": "No se encontró el movimiento con id 99" }""";

    // ----- Generales -----

    static final String ID_INVALIDO = """
            { "mensaje": "El parámetro 'id' no tiene un valor válido" }""";
}
