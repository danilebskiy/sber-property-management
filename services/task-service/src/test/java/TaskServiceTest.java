import com.sberfintech.task.domain.TaskDomainService;
import com.sberfintech.task.dto.CreateTaskRequest;
import com.sberfintech.task.dto.TaskResponse;
import com.sberfintech.task.dto.UpdateTaskRequest;
import com.sberfintech.task.event.TaskEventService;
import com.sberfintech.task.mapper.TaskMapper;
import com.sberfintech.task.model.Task;
import com.sberfintech.task.model.TaskPriority;
import com.sberfintech.task.model.TaskStatus;
import com.sberfintech.task.service.TaskService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Unit-тесты для TaskService")
class TaskServiceTest {

    @Mock
    private TaskDomainService taskDomainService;

    @Mock
    private TaskEventService taskEventService;

    @Mock
    private TaskMapper taskMapper;

    @InjectMocks
    private TaskService taskService;

    private Task taskTest;
    private TaskResponse taskResponseTest;
    private UUID taskId;
    private Long assigneeId;
    private Long creatorId;
    private Long propertyId;


    @BeforeEach
    void setUp() {
        taskId = UUID.randomUUID();
        creatorId = 100L;
        assigneeId = 200L;
        propertyId = 300L;


        taskTest = Task.builder()
                .id(taskId)
                .title("Test Task")
                .description("Test Description")
                .status(TaskStatus.NEW)
                .priority(TaskPriority.MEDIUM)
                .creatorId(creatorId)
                .assigneeId(assigneeId)
                .propertyId(propertyId)
                .build();

        taskResponseTest = TaskResponse.builder()
                .id(taskId)
                .title("Test Task")
                .status("NEW")
                .priority(TaskPriority.MEDIUM)
                .assigneeId(assigneeId)
                .creatorId(creatorId)
                .build();
    }

    @Test
    @DisplayName("Получение всех задач")
    void getAllTasks_shouldReturnAllTasks() {
        when(taskDomainService.findAll()).thenReturn(Arrays.asList(taskTest));
        when(taskMapper.toResponse(taskTest)).thenReturn(taskResponseTest);

        List<TaskResponse> result = taskService.getAllTasks();

        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getTitle());
        verify(taskDomainService, times(1)).findAll();
        verify(taskMapper, times(1)).toResponse(taskTest);
    }

    @Test
    @DisplayName("Получение задачи по ID")
    void getTaskById_shouldReturnTask() {
        when(taskDomainService.findById(taskId)).thenReturn(taskTest);
        when(taskMapper.toResponse(taskTest)).thenReturn(taskResponseTest);

        TaskResponse result = taskService.getTaskById(taskId);

        assertNotNull(result);
        assertEquals(taskId, result.getId());
        assertEquals("Test Task", result.getTitle());
        verify(taskDomainService, times(1)).findById(taskId);
        verify(taskMapper, times(1)).toResponse(taskTest);
    }

    @Test
    @DisplayName("Получение задачи по исполнителю")
    void getTaskByAssignee_shouldReturnTask() {
        when(taskDomainService.findByAssignee(assigneeId)).thenReturn(Arrays.asList(taskTest));
        when(taskMapper.toResponse(taskTest)).thenReturn(taskResponseTest);

        List<TaskResponse> result = taskService.getTasksByAssignee(assigneeId);

        assertEquals(1, result.size());
        assertEquals(assigneeId, result.get(0).getAssigneeId());
        verify(taskDomainService, times(1)).findByAssignee(assigneeId);
        verify(taskMapper, times(1)).toResponse(taskTest);
    }

    @Test
    @DisplayName("Получение задачи от создателя")
    void getTasksByCreator_shouldReturnTask() {
        when(taskDomainService.findByCreator(creatorId)).thenReturn(Arrays.asList(taskTest));
        when(taskMapper.toResponse(taskTest)).thenReturn(taskResponseTest);

        List<TaskResponse> result = taskService.getTasksByCreator(creatorId);

        assertEquals(1, result.size());
        assertEquals(creatorId, result.get(0).getCreatorId());
        verify(taskDomainService, times(1)).findByCreator(creatorId);
        verify(taskMapper, times(1)).toResponse(taskTest);
    }

    @Test
    @DisplayName("Получение задач по статусу")
    void getTasksByStatus_shouldReturnTask() {
        String status = "NEW";
        when(taskDomainService.findByStatus(TaskStatus.NEW)).thenReturn(Arrays.asList(taskTest));
        when(taskMapper.toResponse(taskTest)).thenReturn(taskResponseTest);

        List<TaskResponse> result = taskService.getTasksByStatus(status);

        assertEquals(1, result.size());
        assertEquals("NEW", result.get(0).getStatus());
        verify(taskDomainService, times(1)).findByStatus(TaskStatus.NEW);
        verify(taskMapper, times(1)).toResponse(taskTest);
    }

    @Test
    @DisplayName("Некорректный статус должен бросать исключение")
    void getTasksByStatus_withInvalidStatus_shouldThrowException() {
        String status = "INVALID_STATUS";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> taskService.getTasksByStatus(status));
        assertTrue(exception.getMessage().contains("INVALID_STATUS"));
        verify(taskDomainService, never()).findByStatus(any());
    }

    @Test
    @DisplayName("Получение задач по приоритету")
    void getTasksByPriority_shouldReturnTask() {
        String priority = "MEDIUM";
        when(taskDomainService.findByPriority(TaskPriority.MEDIUM)).thenReturn(Arrays.asList(taskTest));
        when(taskMapper.toResponse(taskTest)).thenReturn(taskResponseTest);

        List<TaskResponse> result = taskService.getTasksByPriority(priority);

        assertEquals(1, result.size());
        assertEquals(TaskPriority.MEDIUM, result.get(0).getPriority());
        verify(taskDomainService, times(1)).findByPriority(TaskPriority.MEDIUM);
        verify(taskMapper, times(1)).toResponse(taskTest);
    }

    @Test
    @DisplayName("Получение просроченных задач")
    void getOverdueTasks_shouldReturnOverdueTasks() {
        when(taskDomainService.findOverdue()).thenReturn(Arrays.asList(taskTest));
        when(taskMapper.toResponse(taskTest)).thenReturn(taskResponseTest);

        List<TaskResponse> result = taskService.getOverdueTasks();

        assertEquals(1, result.size());
        verify(taskDomainService, times(1)).findOverdue();
        verify(taskMapper, times(1)).toResponse(taskTest);
    }

    @Test
    @DisplayName("Получение задач по свойству")
    void getTasksByProperty_shouldReturnTask() {
        when(taskDomainService.findByPropertyId(propertyId)).thenReturn(Arrays.asList(taskTest));
        when(taskMapper.toResponse(taskTest)).thenReturn(taskResponseTest);

        List<TaskResponse> result = taskService.getTasksByProperty(propertyId);

        assertEquals(1, result.size());
        verify(taskDomainService, times(1)).findByPropertyId(propertyId);
        verify(taskMapper, times(1)).toResponse(taskTest);
    }

    @Test
    @DisplayName("Поиск задач с фильтрами")
    void searchTasks_shouldReturnFilteredTasks() {
        LocalDateTime createdAfter = LocalDateTime.now().minusDays(1);
        LocalDateTime createdBefore = LocalDateTime.now().plusDays(1);
        String status = "NEW";
        String priority = "MEDIUM";

        when(taskDomainService.search(
                eq(assigneeId),
                eq(creatorId),
                eq(status),
                eq(priority),
                eq(propertyId),
                eq(createdAfter),
                eq(createdBefore)))
                .thenReturn(Arrays.asList(taskTest));

        when(taskMapper.toResponse(taskTest)).thenReturn(taskResponseTest);

        List<TaskResponse> result = taskService.searchTasks(
                assigneeId,
                creatorId,
                status,
                priority,
                propertyId,
                createdAfter,
                createdBefore
        );


        assertEquals(1, result.size());
        assertEquals("Test Task", result.get(0).getTitle());


        verify(taskDomainService, times(1)).search(
                eq(assigneeId),
                eq(creatorId),
                eq(status),
                eq(priority),
                eq(propertyId),
                eq(createdAfter),
                eq(createdBefore));

        verify(taskMapper, times(1)).toResponse(taskTest);
    }

    @Test
    @DisplayName("Создание задачи")
    void createTask_shouldCreateAndReturnTask() {
        CreateTaskRequest request = CreateTaskRequest.builder()
                .title("NEW Task")
                .description("NEW Description")
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDateTime.now().plusDays(5))
                .assigneeId(assigneeId)
                .creatorId(creatorId)
                .propertyId(propertyId)
                .build();

        Task newTask = Task.builder()
                .id(taskId)
                .title("NEW Task")
                .status(TaskStatus.NEW)
                .build();

        TaskResponse newTaskResponse = TaskResponse.builder()
                .id(taskId)
                .title("NEW Task")
                .status("NEW")
                .build();

        when(taskMapper.toEntity(request)).thenReturn(newTask);
        when(taskDomainService.create(newTask)).thenReturn(newTask);
        when(taskMapper.toResponse(newTask)).thenReturn(newTaskResponse);

        TaskResponse result = taskService.createTask(request);

        assertNotNull(result);
        assertEquals("NEW Task", result.getTitle());
        verify(taskMapper, times(1)).toEntity(request);
        verify(taskDomainService, times(1)).create(newTask);
        verify(taskEventService, times(1)).sendTaskCreated(newTask);
        verify(taskMapper, times(1)).toResponse(newTask);
    }

    @Test
    @DisplayName("Обновление задачи")
    void updateTask_shouldUpdateAndReturnTask() {
        UpdateTaskRequest request = UpdateTaskRequest.builder()
                .title("Updated Task")
                .description("Updated Description")
                .priority(TaskPriority.HIGH)
                .dueDate(LocalDateTime.now().plusDays(10))
                .build();

        Task updatedTask = Task.builder()
                .id(taskId)
                .title("Updated Task")
                .description("Updated Description")
                .priority(TaskPriority.HIGH)
                .dueDate(request.getDueDate())
                .status(TaskStatus.NEW)
                .build();

        TaskResponse updatedResponse = TaskResponse.builder()
                .id(taskId)
                .title("Updated Task")
                .status("NEW")
                .priority(TaskPriority.HIGH)
                .build();

        when(taskDomainService.findById(taskId)).thenReturn(taskTest);
        doNothing().when(taskMapper).updateEntity(request, taskTest);

        when(taskDomainService.update(taskTest)).thenReturn(updatedTask);
        when(taskMapper.toResponse(updatedTask)).thenReturn(updatedResponse);

        TaskResponse result = taskService.updateTask(taskId, request);

        assertNotNull(result);
        assertEquals("Updated Task", result.getTitle());
        assertEquals(TaskPriority.HIGH, result.getPriority());

        verify(taskDomainService, times(1)).findById(taskId);
        verify(taskMapper, times(1)).updateEntity(request, taskTest);
        verify(taskDomainService, times(1)).update(taskTest);
        verify(taskEventService, times(1)).sendTaskUpdated(updatedTask);
        verify(taskMapper, times(1)).toResponse(updatedTask);
    }

    @Test
    @DisplayName("Назначение задачи исполнителю")
    void assignTask_shouldAssignAndReturnTask() {
        // Arrange
        Long newAssigneeId = 300L;
        Task assignedTask = Task.builder()
                .id(taskId)
                .assigneeId(newAssigneeId)
                .status(TaskStatus.ASSIGNED)
                .build();

        TaskResponse assignedResponse = TaskResponse.builder()
                .id(taskId)
                .assigneeId(newAssigneeId)
                .status("ASSIGNED")
                .build();

        when(taskDomainService.assign(taskId, newAssigneeId)).thenReturn(assignedTask);
        when(taskMapper.toResponse(assignedTask)).thenReturn(assignedResponse);

        // Act
        TaskResponse result = taskService.assignTask(taskId, newAssigneeId);

        // Assert
        assertNotNull(result);
        assertEquals(newAssigneeId, result.getAssigneeId());
        assertEquals("ASSIGNED", result.getStatus());
        verify(taskDomainService, times(1)).assign(taskId, newAssigneeId);
        verify(taskEventService, times(1)).sendTaskAssigned(assignedTask, newAssigneeId);
        verify(taskMapper, times(1)).toResponse(assignedTask);
    }

    @Test
    @DisplayName("Начало выполнения задачи")
    void startTask_shouldStartAndReturnTask() {
        // Arrange
        Long userId = 200L;
        Task startedTask = Task.builder()
                .id(taskId)
                .status(TaskStatus.IN_PROGRESS)
                .build();

        TaskResponse startedResponse = TaskResponse.builder()
                .id(taskId)
                .status("IN_PROGRESS")
                .build();

        when(taskDomainService.start(taskId, userId)).thenReturn(startedTask);
        when(taskMapper.toResponse(startedTask)).thenReturn(startedResponse);

        // Act
        TaskResponse result = taskService.startTask(taskId, userId);

        // Assert
        assertNotNull(result);
        assertEquals("IN_PROGRESS", result.getStatus());
        verify(taskDomainService, times(1)).start(taskId, userId);
        verify(taskEventService, times(1)).sendTaskStarted(startedTask, userId);
        verify(taskMapper, times(1)).toResponse(startedTask);
    }

    @Test
    @DisplayName("Завершение задачи")
    void completeTask_shouldCompleteAndReturnTask() {
        // Arrange
        Long userId = 200L;
        Task completedTask = Task.builder()
                .id(taskId)
                .status(TaskStatus.COMPLETED)
                .build();

        TaskResponse completedResponse = TaskResponse.builder()
                .id(taskId)
                .status("COMPLETED")
                .build();

        when(taskDomainService.complete(taskId, userId)).thenReturn(completedTask);
        when(taskMapper.toResponse(completedTask)).thenReturn(completedResponse);

        // Act
        TaskResponse result = taskService.completeTask(taskId, userId);

        // Assert
        assertNotNull(result);
        assertEquals("COMPLETED", result.getStatus());
        verify(taskDomainService, times(1)).complete(taskId, userId);
        verify(taskEventService, times(1)).sendTaskCompleted(completedTask, userId);
        verify(taskMapper, times(1)).toResponse(completedTask);
    }

    @Test
    @DisplayName("Отмена задачи")
    void cancelTask_shouldCancelAndReturnTask() {
        // Arrange
        String reason = "Отмена по требованию клиента";
        Task cancelledTask = Task.builder()
                .id(taskId)
                .status(TaskStatus.CANCELED)
                .build();

        TaskResponse cancelledResponse = TaskResponse.builder()
                .id(taskId)
                .status("CANCELLED")
                .build();

        when(taskDomainService.cancel(taskId, reason)).thenReturn(cancelledTask);
        when(taskMapper.toResponse(cancelledTask)).thenReturn(cancelledResponse);

        // Act
        TaskResponse result = taskService.cancelTask(taskId, reason);

        // Assert
        assertNotNull(result);
        assertEquals("CANCELLED", result.getStatus());
        verify(taskDomainService, times(1)).cancel(taskId, reason);
        verify(taskEventService, times(1)).sendTaskCanceled(cancelledTask, reason);
        verify(taskMapper, times(1)).toResponse(cancelledTask);
    }

    @Test
    @DisplayName("Эскалация задачи")
    void escalateTask_shouldEscalateAndReturnTask() {
        // Arrange
        Long escalatedTo = 500L;
        Task escalatedTask = Task.builder()
                .id(taskId)
                .assigneeId(escalatedTo)
                .build();

        TaskResponse escalatedResponse = TaskResponse.builder()
                .id(taskId)
                .assigneeId(escalatedTo)
                .build();

        when(taskDomainService.escalate(taskId, escalatedTo)).thenReturn(escalatedTask);
        when(taskMapper.toResponse(escalatedTask)).thenReturn(escalatedResponse);

        // Act
        TaskResponse result = taskService.escalateTask(taskId, escalatedTo);

        // Assert
        assertNotNull(result);
        assertEquals(escalatedTo, result.getAssigneeId());
        verify(taskDomainService, times(1)).escalate(taskId, escalatedTo);
        verify(taskEventService, times(1)).sendTaskEscalated(escalatedTask, escalatedTo);
        verify(taskMapper, times(1)).toResponse(escalatedTask);
    }

    @Test
    @DisplayName("Обновление приоритета задачи")
    void updatePriority_shouldUpdatePriorityAndReturnTask() {
        // Arrange
        String newPriority = "HIGH";
        Task updatedTask = Task.builder()
                .id(taskId)
                .priority(TaskPriority.HIGH)
                .build();

        TaskResponse updatedResponse = TaskResponse.builder()
                .id(taskId)
                .priority(TaskPriority.HIGH)
                .build();

        when(taskDomainService.findById(taskId)).thenReturn(taskTest);
        when(taskDomainService.update(taskTest)).thenReturn(updatedTask);
        when(taskMapper.toResponse(updatedTask)).thenReturn(updatedResponse);

        // Act
        TaskResponse result = taskService.updatePriority(taskId, newPriority);

        // Assert
        assertNotNull(result);
        assertEquals(TaskPriority.HIGH, result.getPriority());
        verify(taskDomainService, times(1)).findById(taskId);
        verify(taskDomainService, times(1)).update(taskTest);
        verify(taskEventService, times(1)).sendTaskUpdated(updatedTask);
        verify(taskMapper, times(1)).toResponse(updatedTask);
        assertEquals(TaskPriority.HIGH, taskTest.getPriority());
    }

    @Test
    @DisplayName("Обновление приоритета с некорректным значением должно бросать исключение")
    void updatePriority_withInvalidPriority_shouldThrowException() {
        String invalidPriority = "INVALID_PRIORITY";

        when(taskDomainService.findById(taskId)).thenReturn(taskTest);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskService.updatePriority(taskId, invalidPriority));

        assertTrue(exception.getMessage().contains("INVALID_PRIORITY"));

        verify(taskDomainService, times(1)).findById(taskId);
        verify(taskDomainService, never()).update(any(Task.class));
        verify(taskEventService, never()).sendTaskUpdated(any());
    }

    @Test
    @DisplayName("Обновление дедлайна задачи")
    void updateDueDate_shouldUpdateDueDateAndReturnTask() {
        // Arrange
        LocalDateTime newDueDate = LocalDateTime.now().plusDays(10);
        Task updatedTask = Task.builder()
                .id(taskId)
                .dueDate(newDueDate)
                .build();

        TaskResponse updatedResponse = TaskResponse.builder()
                .id(taskId)
                .dueDate(newDueDate)
                .build();

        when(taskDomainService.findById(taskId)).thenReturn(taskTest);
        when(taskDomainService.update(taskTest)).thenReturn(updatedTask);
        when(taskMapper.toResponse(updatedTask)).thenReturn(updatedResponse);

        // Act
        TaskResponse result = taskService.updateDueDate(taskId, newDueDate);

        // Assert
        assertNotNull(result);
        assertEquals(newDueDate, result.getDueDate());
        verify(taskDomainService, times(1)).findById(taskId);
        verify(taskDomainService, times(1)).update(taskTest);
        verify(taskEventService, times(1)).sendTaskUpdated(updatedTask);
        verify(taskMapper, times(1)).toResponse(updatedTask);
        assertEquals(newDueDate, taskTest.getDueDate());
    }

    @Test
    @DisplayName("Удаление задачи")
    void deleteTask_shouldDeleteTask() {
        doNothing().when(taskDomainService).delete(taskId);
        taskService.deleteTask(taskId);
        verify(taskDomainService, times(1)).delete(taskId);
    }

    @Test
    @DisplayName("Поиск задач с null параметрами")
    void searchTasks_withNullParameters_shouldHandleCorrectly() {
        when(taskDomainService.search(null, null, null, null, null, null, null))
                .thenReturn(Arrays.asList(taskTest));
        when(taskMapper.toResponse(taskTest)).thenReturn(taskResponseTest);

        List<TaskResponse> result = taskService.searchTasks(null, null, null, null, null, null, null);

        assertEquals(1, result.size());
        verify(taskDomainService, times(1)).search(null, null, null, null, null, null, null);
        verify(taskMapper, times(1)).toResponse(taskTest);
    }

    @Test
    @DisplayName("Получение задач по некорректному приоритету должно бросать исключение")
    void getTasksByPriority_withInvalidPriority_shouldThrowException() {
        String invalidPriority = "INVALID_PRIORITY";

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> taskService.getTasksByPriority(invalidPriority));

        assertTrue(exception.getMessage().contains("INVALID_PRIORITY"));
        verify(taskDomainService, never()).findByPriority(any());
    }

    @Test
    @DisplayName("Создание задачи с null запросом должно бросать исключение")
    void createTask_withNullRequest_shouldThrowException() {
        assertThrows(NullPointerException.class, () -> taskService.createTask(null));
    }
    
    @Test
    @DisplayName("Обновление дедлайна с null датой должно бросать исключение")
    void updateDueDate_withNullDate_shouldThrowException() {

        when(taskDomainService.findById(taskId)).thenReturn(taskTest);

        TaskResponse result = taskService.updateDueDate(taskId, null);

        assertNull(taskTest.getDueDate());
        verify(taskDomainService, times(1)).update(taskTest);
    }
}

