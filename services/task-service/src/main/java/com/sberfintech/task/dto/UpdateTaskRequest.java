package com.sberfintech.task.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "Запрос на оьновление задачи")
public class UpdateTaskRequest {

    @Schema(description = "Нвзвание задачи", example = "Ремонт кондиционера срочно!")
    private String title;

    @Schema(description = "Описание задачи")
    private String description;

    @Schema(description = "ID исполнителя задачи", example = "2")
    private Long assigneeId;

    @Schema(description = "ID объекта недвижимости", example = "101")
    private Long propertyId;

    @Schema(description = "ID актива/недвижимости", example = "201")
    private Long assetId;

    @Schema(description = "Приоритет задачи", example = "HIGH")
    private String priority;

    @Schema(description = "Срок выполнения задачи")
    private LocalDateTime dueDate;

    @Schema(description = "Статус задачи", example = "IN_PROGRESS")
    private String status;
}
