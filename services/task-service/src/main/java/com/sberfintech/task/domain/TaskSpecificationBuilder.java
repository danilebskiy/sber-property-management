package com.sberfintech.task.domain;

import com.sberfintech.task.model.Task;
import com.sberfintech.task.model.TaskPriority;
import com.sberfintech.task.model.TaskStatus;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Component
public class TaskSpecificationBuilder {


    public Specification<Task> buildSearchSpecification(Long assigneeId, Long creatorId, String status,
                                                        String priority, Long propertyId, LocalDateTime createdAfter, LocalDateTime createdBefore) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (assigneeId != null) {
                predicates.add(cb.equal(root.get("assigneeId"), assigneeId));
            }
            if (creatorId != null) {
                predicates.add(cb.equal(root.get("creatorId"), creatorId));
            }
            if (status != null) {
                try {
                    TaskStatus taskStatus = TaskStatus.valueOf(status.toUpperCase());
                    predicates.add(cb.equal(root.get("status"), taskStatus));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid status value: " + status);
                }
            }
            if (priority != null) {
                try {
                    TaskPriority taskPriority = TaskPriority.valueOf(priority.toUpperCase());
                    predicates.add(cb.equal(root.get("priority"), taskPriority));
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException("Invalid priority value: " + priority);
                }
            }
            if (propertyId != null) {
                predicates.add(cb.equal(root.get("propertyId"), propertyId));
            }
            if (createdAfter != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createdDate"), createdAfter));
            }
            if (createdBefore != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createdDate"), createdBefore));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

    }
}
