package com.proteccion.crud.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción personalizada para recursos no encontrados.
 * Esta excepción se lanza cuando se intenta acceder a un recurso que no existe
 * en la base de datos o en el sistema.
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    private String resourceName;
    private String fieldName;
    private Object fieldValue;

    /**
     * Constructor con mensaje de error personalizado.
     *
     * @param message El mensaje de error
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor que genera un mensaje de error detallado basado en el recurso,
     * el campo y el valor que causaron la excepción.
     *
     * @param resourceName El nombre del recurso que no se encontró (ej. "Task", "User")
     * @param fieldName El nombre del campo usado en la búsqueda (ej. "id", "username")
     * @param fieldValue El valor del campo que no se encontró
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s no encontrado con %s: '%s'", resourceName, fieldName, fieldValue));
        this.resourceName = resourceName;
        this.fieldName = fieldName;
        this.fieldValue = fieldValue;
    }

    /**
     * Obtiene el nombre del recurso no encontrado.
     *
     * @return El nombre del recurso
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * Obtiene el nombre del campo usado en la búsqueda.
     *
     * @return El nombre del campo
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Obtiene el valor del campo que no se encontró.
     *
     * @return El valor del campo
     */
    public Object getFieldValue() {
        return fieldValue;
    }
}