package com.sberfintech.task.domain;

import com.sberfintech.task.domain.exception.TaskNotFoundException;
import com.sberfintech.task.domain.exception.TaskOperationException;
import com.sberfintech.task.model.Task;
import com.sberfintech.task.model.TaskPriority;
import com.sberfintech.task.model.TaskStatus;
import com.sberfintech.task.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskDomainService {


    private final TaskRepository taskRepository;

    private final TaskValidator taskValidator;

    private final TaskSpecificationBuilder taskSpecificationBuilder;


    public Task findById(UUID id) {
        return taskRepository.findById(id).orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + id));
    }

    public List<Task> findAll() {
        return taskRepository.findAll();
    }

    public List<Task> findByAssignee(Long assigneeId) {
        return taskRepository.findByAssigneeId(assigneeId);
    }

    public List<Task> findByCreator(Long creatorId) {
        return taskRepository.findByCreatorId(creatorId);
    }

    public List<Task> findByPriority(TaskPriority priority) {
        return taskRepository.findByPriority(priority);
    }

    public List<Task> findOverdue() {
        return taskRepository.findOverdueTasks(LocalDateTime.now());
    }

    public List<Task> findByStatus(TaskStatus status) {
        return taskRepository.findByStatus(status);
    }

    public List<Task> findByPropertyId(Long propertyId) {
        return taskRepository.findByPropertyId(propertyId);
    }

    @Transactional
    public Task create(Task task) {
        taskValidator.validateForCreate(task);
        task.setCreationDate(LocalDateTime.now());
        task.setStatus(TaskStatus.NEW);
        return taskRepository.save(task);
    }

    @Transactional
    public Task update(Task task) {
        taskValidator.validateForUpdate(task);
        return taskRepository.save(task);
    }

    @Transactional
    public Task assign(UUID taskId, Long assigneeId) {
        Task task = findById(taskId);
        taskValidator.validateAssignee(task, assigneeId);
        task.setAssigneeId(assigneeId);
        task.setStatus(TaskStatus.ASSIGNED);
        return taskRepository.save(task);
    }

    @Transactional
    public Task start(UUID taskId, Long userId) {
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new TaskNotFoundException("Task not found with id: " + taskId));
        taskValidator.validateForStart(task, userId);
        task.setStatus(TaskStatus.IN_PROGRESS);
        return taskRepository.save(task);
    }

    @Transactional
    public Task complete(UUID taskId, Long userId) {
        Task task = findById(taskId);

        if (!userId.equals(task.getAssigneeId())) {
            throw new TaskOperationException("Only assignee can complete the task");
        }

        task.setStatus(TaskStatus.COMPLETED);
        task.setCompletionDate(LocalDateTime.now());
        return taskRepository.save(task);
    }

    @Transactional
    public Task escalate(UUID taskId, Long escalatedTo) {
        Task task = findById(taskId);

        taskValidator.validateEscalation(task, escalatedTo);

        task.setStatus(TaskStatus.ESCALATED);
        task.setEscalationLevel(task.getEscalationLevel() + 1);
        task.setEscalatedTo(escalatedTo);
        task.setAssigneeId(escalatedTo);

        log.info("Task {} to user {}, level: {}", taskId, escalatedTo, task.getEscalationLevel());
        return taskRepository.save(task);
    }

    @Transactional
    public Task cancel(UUID taskId, String reason) {
        Task task = findById(taskId);

        if (!canBeCancelled(task)){
            throw new TaskOperationException( String.format("Cannot cancel task with status: %s", task.getStatus()));
        }
        task.setStatus(TaskStatus.CANCELED);
        Task cancelledTask = taskRepository.save(task);

        log.info("Task {} cancelled. Reason: {}", taskId, reason);
        return cancelledTask;
    }

    private boolean canBeCancelled(Task task) {
        return task.getStatus() != TaskStatus.COMPLETED &&
                task.getStatus() != TaskStatus.CANCELED;
    }


    @Transactional
    public void delete(UUID taskId) {
        Task task = findById(taskId);

        if (task.getStatus() != TaskStatus.CANCELED && task.getStatus() != TaskStatus.COMPLETED) {
            throw new TaskOperationException("Cannot delete task with status: " + task.getStatus());
        }
        taskRepository.delete(task);
    }

    public List<Task> search(Long assigneeId, Long creatorId, String status,
                             String priority, Long propertyId, LocalDateTime createdAfter, LocalDateTime createdBefore) {

        Specification<Task> spec = taskSpecificationBuilder.buildSearchSpecification(assigneeId, creatorId, status, priority,
                propertyId, createdAfter, createdBefore);

        return taskRepository.findAll(spec);
    }

}
