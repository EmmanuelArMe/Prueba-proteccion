package com.proteccion.crud.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Punto de entrada para manejar errores de autenticación JWT.
 * Esta clase se encarga de gestionar las excepciones de autenticación
 * y devolver respuestas adecuadas cuando la autenticación falla.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationEntryPoint.class);

    /**
     * Este método se invoca cuando un usuario intenta acceder a un recurso protegido sin autenticación válida.
     * Devuelve un código de estado 401 (No autorizado) al cliente.
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {
        logger.error("Error de autenticación: {}", authException.getMessage());
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado: " + authException.getMessage());
    }
}
