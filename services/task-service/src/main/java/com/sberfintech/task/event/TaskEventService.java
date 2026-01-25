package com.sberfintech.task.event;

import com.sberfintech.task.dto.EventType;
import com.sberfintech.task.dto.TaskEvent;
import com.sberfintech.task.model.Task;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskEventService {


    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;

    public void sendTaskCreated(Task task){
        sendEvent(task, EventType.TASK_CREATED, task.getCreatorId(), null);
    }

    public void sendTaskUpdated(Task task){
        sendEvent(task, EventType.TASK_UPDATED, task.getCreatorId(), null);
    }

    public void sendTaskAssigned(Task task, Long assigneeId){
        sendEvent(task, EventType.TASK_ASSIGNED, assigneeId, null);
    }

    public void sendTaskStarted(Task task, Long userId){
        sendEvent(task, EventType.TASK_STARTED, userId, null);
    }

    public void sendTaskCompleted(Task task, Long userId){
        sendEvent(task, EventType.TASK_COMPLETED, userId, null);
    }

    public void sendTaskCanceled(Task task, String reason){
        sendEvent(task, EventType.TASK_CANCELED, task.getCreatorId(), reason);
    }

    public void sendTaskEscalated(Task task, Long escalatedTo){
        sendEvent(task, EventType.TASK_ESCALATED, escalatedTo, null);
    }

    public void sendTaskOverdue(Task task){
        sendEvent(task, EventType.TASK_OVERDUE, task.getAssigneeId(),
                "Задача просрочена. Срок выполнения: " + task.getDueDate());
    }



    private void sendEvent(Task task, EventType eventType, Long userId, String reason){
        TaskEvent event = TaskEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(eventType)
                .taskId(task.getId())
                .userId(userId)
                .description(String.format("Task %s: %s", task.getTitle(), eventType))
                .reason(reason)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send("task-events", task.getId().toString(), event);
        log.info("Sent task event: {} for task {}. Reason: {}", eventType, task.getId(), reason);
    }
}
