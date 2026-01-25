package com.sberfintech.task.dto;

import com.sberfintech.task.model.TaskPriority;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@Schema(description = "Запрос на создание задачи")
public class CreateTaskRequest {

    @NotBlank(message = "Title is required")
    @Schema(description = "Название задачи", example = "Ремонт кондиционера")
    private String title;

    @Schema(description = "Описание задачи", example = "Необходимо заменить фильтры в кондиционере")
    private String description;

    @NotNull(message = "Creator ID is required")
    @Schema(description = "ID создателя задачи", example = "1")
    private Long creatorId;

    @Schema(description = "ID сполнителя задачи", example = "2")
    private Long assigneeId;

    @Schema(description = "ID объекта недвижимости", example = "101")
    private Long propertyId;

    @Schema(description = "ID актива/оборудования", example = "201")
    private Long assetId;

    @Schema(description = "Приоритет задачи", example = "HIGH")
    private TaskPriority priority;

    @Schema(description = "Срок выполнения задачи")
    private LocalDateTime dueDate;

}
