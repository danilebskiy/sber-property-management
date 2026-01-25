package com.sberfintech.task.controller;

import com.sberfintech.task.dto.CreateTaskRequest;
import com.sberfintech.task.dto.TaskResponse;
import com.sberfintech.task.dto.UpdateTaskRequest;
import com.sberfintech.task.service.TaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
@RequiredArgsConstructor
@Slf4j
@Validated
@Tag(name = "Task Management", description = "API для управления задачами по обслуживанию недвижимости")
public class TaskController {


    private final TaskService taskService;

    //==================== GET ENDPOINTS =====================


    @GetMapping
    @Operation(summary = "Получить все задачи", description = "Возвращает список всех задач")
    @ApiResponse(responseCode = "200", description = "Успешный запрос")
    public List<TaskResponse> getAllTasks() {
        log.info("Получен запрос на получение всех задач");
        List<TaskResponse> tasks = taskService.getAllTasks();
        log.info("Найдено {} задач", tasks.size());
        return tasks;
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить задачу по ID", description = "Возвращает задачу по указанному идентификатору")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Задача найдена"),
            @ApiResponse(responseCode = "404", description = "Задача не найдена")
    })
    public TaskResponse getTaskById(@Parameter(description = "ID задачи", example = "1")
                                    @PathVariable Long id) {

        log.info("Получен запрос на получение задачи с ID {}", id);
        return taskService.getTaskById(id);
    }

    @GetMapping("/assignee/{assigneeId}")
    @Operation(summary = "Получить задачи исполнителя", description = "Возвращает все задачи, назначенные указанному исполнителю")
    @ApiResponse(responseCode = "200", description = "Успешный запрос")
    public List<TaskResponse> getTasksByAssignee(
            @Parameter(description = "ID исполнителя", example = "2")
            @PathVariable Long assigneeId) {

        log.info("Получен запрос на получение задач для исполнителя ID {}", assigneeId);
        return taskService.getTasksByAssignee(assigneeId);
    }

    @GetMapping("/creator/{creatorId}")
    @Operation(summary = "Получить задачи создателя", description = "Возвращает все задачи, созданные указанным пользователем")
    @ApiResponse(responseCode = "200", description = "Успешный запрос")
    public List<TaskResponse> getTasksByCreator(
            @Parameter(description = "ID создателя", example = "1")
            @PathVariable Long creatorId) {

        log.info("Получен запрос на получение задач для создателя ID {}", creatorId);
        return taskService.getTasksByCreator(creatorId);
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Получить задачи по статусу", description = "Возвращает задачи с указанным статусом")
    @ApiResponse(responseCode = "200", description = "Успешный запрос")
    public List<TaskResponse> getTasksByStatus(
            @Parameter(description = "Статус задачи", example = "IN_PROGRESS")
            @PathVariable String status) {

        log.info("Получен запрос на получение задач со статусом {}", status);
        return taskService.getTasksByStatus(status);
    }

    @GetMapping("/priority/{priority}")
    @Operation(summary = "Получить задачи по приоритету", description = "Возвращает задачи с указанным приоритетом")
    @ApiResponse(responseCode = "200", description = "Успешный запрос")
    public List<TaskResponse> getTasksByPriority(
            @Parameter(description = "Приоритет задачи", example = "HIGH")
            @PathVariable String priority) {

        log.info("Получен запрос на получение задач с приоритетом {}", priority);
        return taskService.getTasksByPriority(priority);
    }

    @GetMapping("/overdue")
    @Operation(summary = "Получить просроченные задачи", description = "Возвращает задачи, у которых истек срок выполнения")
    @ApiResponse(responseCode = "200", description = "Успешный запрос")
    public List<TaskResponse> getOverdueTasks() {

        log.info("Получен запрос на получение просроченных задач");
        return taskService.getOverdueTasks();
    }

    @GetMapping("/property/{propertyId}")
    @Operation(summary = "Получить задачи по объекту недвижимости",
            description = "Возвращает задачи, связанные с указанным объектом недвижимости")
    @ApiResponse(responseCode = "200", description = "Успешный запрос")
    public List<TaskResponse> getTasksByProperty(
            @Parameter(description = "ID объекта недвижимости", example = "101")
            @PathVariable Long propertyId) {

        log.info("Получен запрос на получение задач для объекта недвижимости ID {}", propertyId);
        return taskService.getTasksByProperty(propertyId);
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск задач по критериям",
            description = "Возвращает задачи, отфильтрованные по различным критериям")
    @ApiResponse(responseCode = "200", description = "Успешный запрос")
    public List<TaskResponse> searchTasks(
            @Parameter(description = "ID исполнителя", example = "2")
            @RequestParam(required = false) Long assigneeId,

            @Parameter(description = "ID создателя", example = "1")
            @RequestParam(required = false) Long creatorId,

            @Parameter(description = "Статус задачи", example = "IN_PROGRESS")
            @RequestParam(required = false) String status,

            @Parameter(description = "Приоритет задачи", example = "HIGH")
            @RequestParam(required = false) String priority,

            @Parameter(description = "ID объекта недвижимости", example = "101")
            @RequestParam(required = false) Long propertyId,

            @Parameter(description = "Дата создания от", example = "2024-01-01T00:00:00")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAfter,

            @Parameter(description = "Дата создания до", example = "2024-12-31T23:59:59")
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdBefore) {

        log.info("Поиск задач с параметрами: assigneeId={}, creatorId={}, status={}, priority={}, propertyId={}",
                assigneeId, creatorId, status, priority, propertyId);
        return taskService.searchTasks(assigneeId, creatorId, status, priority, propertyId, createdAfter, createdBefore);
    }

    //==================== POST ENDPOINTS =========================

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Создать новую задачу", description = "Создает новую задачу")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Задача успешно создана"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные запроса")
    })
    public TaskResponse createTask(
            @Parameter(description = "Данные для создания задачи")
            @Valid @RequestBody CreateTaskRequest request) {

        log.info("Создание задачи {}", request.getTitle());
        TaskResponse createdTask = taskService.createTask(request);
        log.info("Задача создана с ID {}", createdTask.getId());
        return createdTask;
    }

    @PostMapping("/{id}/assign/{assigneeId}")
    @Operation(summary = "Назначить задачу исполнителю", description = "Назначает задачу указанному исполнителю")
    @ApiResponse(responseCode = "200", description = "Задача успешно назначена")
    public TaskResponse assignTask(
            @Parameter(description = "ID задачи", example = "1")
            @PathVariable Long id,

            @Parameter(description = "ID исполнителя", example = "2")
            @PathVariable Long assigneeId) {

        log.info("Назначение задачи {} исполнителю {}", id, assigneeId);
        return taskService.assignTask(id, assigneeId);
    }

    @PostMapping("/{id}/start")
    @Operation(summary = "Начать выполнение задачи", description = "Изменяет статус задачи на 'В работе'")
    @ApiResponse(responseCode = "200", description = "Выполнение задачи начато")
    public TaskResponse startTask(
            @Parameter(description = "ID задачи", example = "1")
            @PathVariable Long id,

            @Parameter(description = "ID пользователя", example = "2")
            @RequestParam Long userId) {

        log.info("Начало выполнения задачи {} пользователем {}", id, userId);
        return taskService.startTask(id, userId);
    }

    @PostMapping("/{id}/complete")
    @Operation(summary = "Завершить задачу", description = "Изменяет статус задачи на 'Выполнена'")
    @ApiResponse(responseCode = "200", description = "Задача успешно завершена")
    public TaskResponse completeTask(
            @Parameter(description = "ID задачи", example = "1")
            @PathVariable Long id,

            @Parameter(description = "ID пользователя")
            @RequestParam Long userId) {

        log.info("Завершение задачи {} пользователем {}", id, userId);
        return taskService.completeTask(id, userId);
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Отменить задачу", description = "Изменяет статус задачи на 'Отменена'")
    @ApiResponse(responseCode = "200", description = "Задача успешно отменена")
    public TaskResponse cancelTask(
            @Parameter(description = "ID задачи", example = "1")
            @PathVariable Long id,

            @Parameter(description = "Причина отмены", example = "Изменение планов")
            @RequestParam(required = false) String reason) {

        log.info("Отмена задачи {} с причиной {}", id, reason);
        return taskService.cancelTask(id, reason);
    }

    @PostMapping("/{id}/escalate")
    @Operation(summary = "Эскалировать задачу", description = "Эскалирует задачу следующему ответственному лицу")
    @ApiResponse(responseCode = "200", description = "Задача успешно эскалирована")
    public TaskResponse escalateTask(
            @Parameter(description = "ID задачи", example = "1")
            @PathVariable Long id,

            @Parameter(description = "ID пользователя", example = "3")
            @RequestParam(required = false) Long escalatedTo) {

        log.info("Эскалация задачи {}", id);
        return taskService.escalateTask(id, escalatedTo);
    }

    //========================= PUT ENDPOINTS =======================

    @PutMapping("/{id}")
    @Operation(summary = "Обновить задачу", description = "Обновляет информацию о задаче")
    @ApiResponse(responseCode = "200", description = "Задача успешно обновлена")
    public TaskResponse updateTask(
            @Parameter(description = "ID задачи", example = "1")
            @PathVariable Long id,

            @Parameter(description = "Данные для обновления задачи")
            @Valid @RequestBody UpdateTaskRequest request) {

        log.info("Обновление задачи {}", id);
        return taskService.updateTask(id, request);
    }

    //===================== PATCH ENDPOINTS ====================

    @PatchMapping("/{id}/priority")
    @Operation(summary = "Изменить приоритет задачи", description = "Изменяет приоритет задачи")
    @ApiResponse(responseCode = "200", description = "Приоритет успешно изменен")
    public TaskResponse updatePriority(
            @Parameter(description = "ID задачи", example = "1")
            @PathVariable Long id,

            @Parameter(description = "Новый приоритет", example = "CRITICAL")
            @RequestParam String priority) {

        log.info("Изменение приоритета задачи {} на {}", id, priority);
        return taskService.updatePriority(id, priority);
    }

    @PatchMapping("/{id}/due-date")
    @Operation(summary = "Изменить срок выполнения", description = "Изменяет срок выполнения задачи")
    @ApiResponse(responseCode = "200", description = "Срок выполнения успешно изменен")
    public TaskResponse updateDueDate(
            @Parameter(description = "ID задачи", example = "1")
            @PathVariable Long id,

            @Parameter(description = "Новый срок выполнения", example = "2024-12-31T23:59:59")
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dueDate) {

        log.info("Изменение срока выполнения задачи {} на {}", id, dueDate);
        return taskService.updateDueDate(id, dueDate);
    }

    //========================== DELETE ENDPOINTS ============================

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удалить задачу", description = "Удаляет задачу по заданному идентификатору")
    @ApiResponse(responseCode = "204", description = "Задача успешно удалена")
    public void deleteTask(
            @Parameter(description = "ID задачи", example = "1")
            @PathVariable Long id) {

        log.info("Удаление задачи {}", id);
        taskService.deleteTask(id);
    }

}
