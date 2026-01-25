package com.sberfintech.task.domain;

import com.sberfintech.task.domain.exception.TaskOperationException;
import com.sberfintech.task.model.Task;
import com.sberfintech.task.model.TaskPriority;
import com.sberfintech.task.model.TaskStatus;
import jakarta.validation.ValidationException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TaskValidator {

    public void validateForCreate(Task task){
        if (task == null){
            throw new IllegalArgumentException("Task object cannot be null");
        }
        if (task.getTitle() == null || task.getTitle().trim().isEmpty()){
            throw new ValidationException("Task title is required");
        }
        if (task.getCreatorId() == null){
            throw new ValidationException("Creator id is required");
        }
        if (task.getDueDate() != null && task.getDueDate().isBefore(LocalDateTime.now())){
            throw new ValidationException("Due date cannot be in the past");
        }
        if (task.getPriority() == null){
            task.setPriority(TaskPriority.MEDIUM);
        }
    }

    public void validateForUpdate(Task task){
        if (task == null){
            throw new IllegalArgumentException("Task object cannot be null");
        }
        if (task.getId() == null){
            throw new IllegalArgumentException("Task ID cannot be null for update operation");
        }
        if (task.getStatus() == TaskStatus.COMPLETED){
            throw new TaskOperationException("Task status cannot be COMPLETED for update operation");
        }
        if (task.getStatus() == TaskStatus.CANCELED){
            throw new TaskOperationException("Task status cannot be CANCELED for update operation");
        }

    }

    public void validateAssignee(Task task, Long assigneeId){
        if (assigneeId == null) {
            throw new IllegalArgumentException("Assignee ID cannot be null");
        }
        if (task.getAssigneeId() != null) {
            throw new IllegalArgumentException("Task is already assigned");
        }
        if (task.getStatus() != TaskStatus.NEW){
            throw new IllegalArgumentException("Only NEW tasks can be assigned");
        }
    }
    public void validateForStart(Task task,Long userId){
        if (task == null){
            throw new IllegalArgumentException("Task object cannot be null");
        }
        if (userId == null) {
            throw new IllegalArgumentException("User ID cannot be null");
        }
        if (!userId.equals(task.getAssigneeId())) {
            throw new TaskOperationException("Only assignee can start the task");
        }
        if (task.getStatus() != TaskStatus.IN_PROGRESS){
            throw new IllegalStateException("Only IN_PROGRESS tasks can be start");
        }
        if (!canBeStarted(task.getStatus())){
            throw new IllegalStateException(String.format("Task [ID=%s] with status '%s' cannot be started",
                    task.getId(), task.getStatus()));
        }
    }

    private boolean canBeStarted(TaskStatus status){
        return status == TaskStatus.NEW || status == TaskStatus.ASSIGNED;
    }

    public void validateEscalation(Task task, Long escalatedTo){
        if (task.getStatus() == TaskStatus.COMPLETED){
            throw new TaskOperationException("Task has already been escalated");
        }
        if (task.getStatus() == TaskStatus.CANCELED){
            throw new TaskOperationException("You cannot escalate a canceled task");
        }
        if (task.getAssigneeId() != null && task.getAssigneeId().equals(escalatedTo)){
            throw new TaskOperationException("You can't escalate a problem to yourself");
        }
    }
}
