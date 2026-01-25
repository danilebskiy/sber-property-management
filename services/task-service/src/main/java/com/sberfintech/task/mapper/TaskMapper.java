package com.sberfintech.task.mapper;

import com.sberfintech.task.dto.CreateTaskRequest;
import com.sberfintech.task.dto.TaskResponse;
import com.sberfintech.task.dto.UpdateTaskRequest;
import com.sberfintech.task.model.Task;
import com.sberfintech.task.model.TaskPriority;
import com.sberfintech.task.model.TaskStatus;
import org.mapstruct.*;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TaskMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", constant = "NEW")
    @Mapping(target = "creationDate", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "createdAt",  ignore = true)
    @Mapping(target = "updatedAt",  ignore = true)
    @Mapping(target = "completionDate",  ignore = true)
    @Mapping(target = "escalationLevel", constant = "0")
    @Mapping(target = "escalatedTo", ignore = true)
    @Mapping(target = "priority", source = "priority", qualifiedByName = "mapPriority")
    Task toEntity(CreateTaskRequest request);

    @Mapping(target = "id",  ignore = true)
    @Mapping(target = "creationDate",  ignore = true)
    @Mapping(target = "createdAt",  ignore = true)
    @Mapping(target = "updatedAt",  ignore = true)
    @Mapping(target = "completionDate",  ignore = true)
    @Mapping(target = "escalationLevel", ignore = true)
    @Mapping(target = "escalatedTo", ignore = true)
    @Mapping(target = "creatorId", ignore = true)
    @Mapping(target = "priority", source = "priority", qualifiedByName = "mapPriority")
    @Mapping(target = "status", source = "status", qualifiedByName = "mapStatus")
    void updateEntity(UpdateTaskRequest request, @MappingTarget Task task);

    TaskResponse toResponse(Task task);


    @Named("mapPriority")
    default TaskPriority mapPriority(String priority){
        if (priority == null){
            return TaskPriority.MEDIUM;
        }
        try {
            return TaskPriority.valueOf(priority.toUpperCase());
        }catch (IllegalArgumentException e){
            return TaskPriority.MEDIUM;
        }
    }

    @Named("mapStatus")
    default TaskStatus mapStatus(String status){
        if (status == null){
            return null;
        }
        try {
            return TaskStatus.valueOf(status.toUpperCase());
        }catch (IllegalArgumentException e){
            return null;
        }
    }

}
