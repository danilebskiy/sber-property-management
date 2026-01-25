package com.sberfintech.task.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.sberfintech.task.model.TaskPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Schema(description = "Ответ с информацией о задаче")
public class TaskResponse {

    @Schema(description = "ID задачи", example = "1")
    private UUID id;

    @Schema(description = "Описание задачи")
    private String description;

    @Schema(description = "Статус задачи", example = "NEW")
    private String status;

    @Schema(description = "Приоритет задачи", example = "HIGH")
    private TaskPriority priority;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата создания")
    private LocalDateTime creationDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Срок выполнения")
    private LocalDateTime dueDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Schema(description = "Дата заверешения")
    private LocalDateTime completionDate;

    @Schema(description = "ID создателя", example = "1")
    private Long creatorId;

    @Schema(description = "ID объекта недвижимости", example = "101")
    private Long propertyId;

    @Schema(description = "ID актива", example = "201")
    private Long assetId;

    @Schema(description = "Уровень эскалации", example = "0")
    private Integer escalationLevel;
}
