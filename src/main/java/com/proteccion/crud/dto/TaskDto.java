package com.proteccion.crud.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskDto {
    private Long id;

    @NotBlank(message = "El título es obligatorio")
    private String title;

    private String description;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    private LocalDate dueDate;

    private String status;

    private Long createdById;
    private String createdByUsername;

    private Long assignedToId;
    private String assignedToUsername;
}
