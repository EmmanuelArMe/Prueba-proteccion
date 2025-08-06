package com.proteccion.crud.exception;

import java.util.Date;

/**
 * Clase que representa los detalles de un error para las respuestas de la API.
 * Esta clase es utilizada por el GlobalExceptionHandler para formatear las respuestas
 * de error de manera consistente.
 */
public class ErrorDetails {
    private final Date timestamp;
    private final String message;
    private final String details;

    /**
     * Constructor para crear un objeto de detalles de error.
     *
     * @param timestamp Marca de tiempo cuando ocurrió el error
     * @param message Mensaje de error descriptivo
     * @param details Detalles adicionales sobre el error o la solicitud
     */
    public ErrorDetails(Date timestamp, String message, String details) {
        this.timestamp = timestamp;
        this.message = message;
        this.details = details;
    }

    /**
     * Obtiene la marca de tiempo del error.
     *
     * @return La marca de tiempo
     */
    public Date getTimestamp() {
        return timestamp;
    }

    /**
     * Obtiene el mensaje de error.
     *
     * @return El mensaje de error
     */
    public String getMessage() {
        return message;
    }

    /**
     * Obtiene los detalles adicionales del error.
     *
     * @return Los detalles del error
     */
    public String getDetails() {
        return details;
    }
}
