package com.sberfintech.task.service;

import com.sberfintech.task.domain.TaskDomainService;
import com.sberfintech.task.dto.CreateTaskRequest;
import com.sberfintech.task.dto.TaskResponse;
import com.sberfintech.task.dto.UpdateTaskRequest;
import com.sberfintech.task.event.TaskEventService;
import com.sberfintech.task.mapper.TaskMapper;
import com.sberfintech.task.model.Task;
import com.sberfintech.task.model.TaskPriority;
import com.sberfintech.task.model.TaskStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {


    private final TaskDomainService taskDomainService;
    private final TaskEventService taskEventService;
    private final TaskMapper taskMapper;


    public List<TaskResponse> getAllTasks() {
        return taskDomainService.findAll().stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    public TaskResponse getTaskById(UUID Id) {
        Task task = taskDomainService.findById(Id);
        return taskMapper.toResponse(task);
    }

    public List<TaskResponse> getTasksByAssignee(Long assigneeId) {
        return taskDomainService.findByAssignee(assigneeId).stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByCreator(Long creatorId) {
        return taskDomainService.findByCreator(creatorId).stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByStatus(String status) {
        TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
        return taskDomainService.findByStatus(taskStatus).stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByPriority(String priority) {
        TaskPriority taskPriority = TaskPriority.valueOf(priority.toUpperCase());
        return taskDomainService.findByPriority(taskPriority).stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getOverdueTasks(){
        return taskDomainService.findOverdue().stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    public List<TaskResponse> getTasksByProperty(Long propertyId) {
        return taskDomainService.findByPropertyId(propertyId).stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }


    public List<TaskResponse> searchTasks(Long assigneeId, Long creatorId, String status,
                                          String priority, Long propertyId,
                                          LocalDateTime createdAfter, LocalDateTime createdBefore) {
        return taskDomainService.search(assigneeId, creatorId, status, priority,
                propertyId, createdAfter, createdBefore).stream()
                .map(taskMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public TaskResponse createTask(CreateTaskRequest request) {
        Task task = taskMapper.toEntity(request);
        Task savedTask = taskDomainService.create(task);
        taskEventService.sendTaskCreated(savedTask);
        log.info("Created task with id: {}", savedTask.getId());
        return taskMapper.toResponse(savedTask);
    }

    @Transactional
    public TaskResponse updateTask(UUID id, UpdateTaskRequest request) {
        Task task = taskDomainService.findById(id);
        taskMapper.updateEntity(request, task);
        Task updatedTask = taskDomainService.update(task);
        taskEventService.sendTaskUpdated(updatedTask);
        return taskMapper.toResponse(updatedTask);
    }

    @Transactional
    public TaskResponse assignTask(UUID taskId, Long assigneeId) {
        Task task = taskDomainService.assign(taskId, assigneeId);
        taskEventService.sendTaskAssigned(task, assigneeId);
        return taskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse startTask(UUID taskId, Long userId) {
        Task task = taskDomainService.start(taskId, userId);
        taskEventService.sendTaskStarted(task, userId);
        return taskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse completeTask(UUID taskId, Long userId) {
        Task task = taskDomainService.complete(taskId, userId);
        taskEventService.sendTaskCompleted(task, userId);
        return taskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse cancelTask(UUID taskId, String reason) {
        Task task = taskDomainService.cancel(taskId, reason);
        taskEventService.sendTaskCanceled(task, reason);
        return taskMapper.toResponse(task);
    }
    @Transactional
    public TaskResponse escalateTask(UUID taskId, Long escalatedTo) {
        Task task = taskDomainService.escalate(taskId, escalatedTo);
        taskEventService.sendTaskEscalated(task, escalatedTo);
        return taskMapper.toResponse(task);
    }

    @Transactional
    public TaskResponse updatePriority(UUID taskId, String priority) {
        Task task = taskDomainService.findById(taskId);
        TaskPriority taskPriority = TaskPriority.valueOf(priority.toUpperCase());
        task.setPriority(taskPriority);
        Task updatedTask = taskDomainService.update(task);
        taskEventService.sendTaskUpdated(updatedTask);
        return taskMapper.toResponse(updatedTask);
    }

    @Transactional
    public TaskResponse updateDueDate(UUID taskId, LocalDateTime dueDate) {
        Task task = taskDomainService.findById(taskId);
        task.setDueDate(dueDate);
        Task updatedTask = taskDomainService.update(task);
        taskEventService.sendTaskUpdated(updatedTask);
        return taskMapper.toResponse(updatedTask);
    }

    @Transactional
    public void deleteTask(UUID taskId) {
        taskDomainService.delete(taskId);
        log.info("Deleted task with id: {}", taskId);
    }
}
