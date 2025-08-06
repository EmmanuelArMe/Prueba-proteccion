package com.proteccion.crud.repository;

import com.proteccion.crud.models.Task;
import com.proteccion.crud.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    List<Task> findByAssignedTo(User user);
    List<Task> findByCreatedBy(User user);
    List<Task> findByAssignedToOrCreatedBy(User assignedTo, User createdBy);
    List<Task> findByStatus(Task.TaskStatus status);
}
