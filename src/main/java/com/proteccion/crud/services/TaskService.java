package com.proteccion.crud.services;

import com.proteccion.crud.dto.TaskDto;
import com.proteccion.crud.exception.ResourceNotFoundException;
import com.proteccion.crud.models.Task;
import com.proteccion.crud.models.User;
import com.proteccion.crud.repository.TaskRepository;
import com.proteccion.crud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserRepository userRepository;

    private final String USER_NOT_FOUND = "Usuario no encontrado";
    private final String ROLE_ADMIN = "ROLE_ADMIN";

    public List<TaskDto> getAllTasks() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        // Si es admin, puede ver todas las tareas
        if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals(ROLE_ADMIN))) {
            return taskRepository.findAll().stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }

        // Si es usuario normal, solo ve sus tareas asignadas o creadas por él
        return taskRepository.findByAssignedToOrCreatedBy(currentUser, currentUser).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con id: " + id));

        // Verificar si el usuario tiene acceso a esta tarea
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals(ROLE_ADMIN));
        boolean isCreator = task.getCreatedBy().getId().equals(currentUser.getId());
        boolean isAssigned = task.getAssignedTo().getId().equals(currentUser.getId());

        if (isAdmin || isCreator || isAssigned) {
            return convertToDto(task);
        } else {
            throw new ResourceNotFoundException("No tienes acceso a esta tarea");
        }
    }

    public TaskDto createTask(TaskDto taskDto) {
        Task task = convertToEntity(taskDto);

        // Establecer el usuario actual como creador
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        task.setCreatedBy(currentUser);

        // Si se asigna a otro usuario
        if (taskDto.getAssignedToId() != null) {
            User assignedUser = userRepository.findById(taskDto.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario asignado no encontrado"));
            task.setAssignedTo(assignedUser);
        } else {
            // Si no se asigna, se autoasigna
            task.setAssignedTo(currentUser);
        }

        Task savedTask = taskRepository.save(task);
        return convertToDto(savedTask);
    }

    public TaskDto updateTask(Long id, TaskDto taskDto) {
        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con id: " + id));

        // Verificar si el usuario tiene permiso para actualizar
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals(ROLE_ADMIN));
        boolean isCreator = existingTask.getCreatedBy().getId().equals(currentUser.getId());
        boolean isAssigned = existingTask.getAssignedTo().getId().equals(currentUser.getId());

        if (!(isAdmin || isCreator || isAssigned)) {
            throw new ResourceNotFoundException("No tienes permiso para actualizar esta tarea");
        }

        if (taskDto.getTitle() != null) {
            existingTask.setTitle(taskDto.getTitle());
        }

        if (taskDto.getDescription() != null) {
            existingTask.setDescription(taskDto.getDescription());
        }

        if (taskDto.getDueDate() != null) {
            existingTask.setDueDate(taskDto.getDueDate());
        }

        if (taskDto.getStatus() != null) {
            existingTask.setStatus(Task.TaskStatus.valueOf(taskDto.getStatus()));
        }

        // Solo admin o creador puede cambiar asignación
        if (taskDto.getAssignedToId() != null && (isAdmin || isCreator)) {
            User assignedUser = userRepository.findById(taskDto.getAssignedToId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario asignado no encontrado"));
            existingTask.setAssignedTo(assignedUser);
        }

        Task updatedTask = taskRepository.save(existingTask);
        return convertToDto(updatedTask);
    }

    public void deleteTask(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tarea no encontrada con id: " + id));

        // Verificar si el usuario tiene permiso para eliminar
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(r -> r.getAuthority().equals(ROLE_ADMIN));
        boolean isCreator = task.getCreatedBy().getId().equals(currentUser.getId());

        if (!(isAdmin || isCreator)) {
            throw new ResourceNotFoundException("No tienes permiso para eliminar esta tarea");
        }

        taskRepository.delete(task);
    }

    // Filtrar tareas por estado
    public List<TaskDto> getTasksByStatus(String status) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND));

        Task.TaskStatus taskStatus = Task.TaskStatus.valueOf(status);

        // Si es admin, puede ver todas las tareas con ese estado
        if (auth.getAuthorities().stream().anyMatch(r -> r.getAuthority().equals(ROLE_ADMIN))) {
            return taskRepository.findByStatus(taskStatus).stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }

        // Si es usuario normal, filtra entre sus tareas
        return taskRepository.findByAssignedToOrCreatedBy(currentUser, currentUser).stream()
                .filter(task -> task.getStatus() == taskStatus)
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    // Método para convertir entidad a DTO
    private TaskDto convertToDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setDueDate(task.getDueDate());
        dto.setStatus(task.getStatus().name());

        if (task.getCreatedBy() != null) {
            dto.setCreatedById(task.getCreatedBy().getId());
            dto.setCreatedByUsername(task.getCreatedBy().getUsername());
        }

        if (task.getAssignedTo() != null) {
            dto.setAssignedToId(task.getAssignedTo().getId());
            dto.setAssignedToUsername(task.getAssignedTo().getUsername());
        }

        return dto;
    }

    // Método para convertir DTO a entidad
    private Task convertToEntity(TaskDto dto) {
        Task task = new Task();
        task.setTitle(dto.getTitle());
        task.setDescription(dto.getDescription());
        task.setDueDate(dto.getDueDate());

        if (dto.getStatus() != null) {
            task.setStatus(Task.TaskStatus.valueOf(dto.getStatus()));
        } else {
            task.setStatus(Task.TaskStatus.TODO);
        }

        return task;
    }
}
