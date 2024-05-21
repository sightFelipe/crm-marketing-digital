package com.crmmarketingdigitalback2024.commons.constants.endPoints.user;

public interface IUserEndPoint {
    String USER_BASE_URL = "/usuario";
    String TOGGLE_USER_STATUS = "/{id}/cambiarEstado";
    String ENABLED_USERS_URL = "/activos";
    String DISABLED_USERS_URL = "/inactivos";
    String USER_CRATE = "/crear-usuario";
    String USER_SERVICE = "/servicio-usuario";
    String USER_UPDATE = "/actualizar-usuario";
    String USER_DELETE = "/eliminar-usuario/{userId}";
    String USER_PASSWORD_RESET_REQUEST = "/solicitar-restablecer-contraseña";
    String USER_PASSWORD_RESET = "/restablecer-contraseña";
}
