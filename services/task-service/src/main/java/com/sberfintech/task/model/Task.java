package com.sberfintech.task.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tasks")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Task {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TaskPriority priority;

    @CreationTimestamp
    @Column(name = "creation_date", updatable = false)
    private LocalDateTime creationDate;

    @Column(name = "due_date")
    private LocalDateTime dueDate;

    @Column(name = "completion_date")
    private LocalDateTime completionDate;

    @Column(name = "assignee_id")
    private Long assigneeId;

    @Column(name = "creator_id", nullable = false)
    private Long creatorId;

    @Column(name = "property_id")
    private Long propertyId;

    @Column(name = "asset_id")
    private Long assetId;

    @Column(name = "escalation_level")
    @Builder.Default
    private Integer escalationLevel = 0;

    @Column(name = "escalated_to")
    private Long escalatedTo;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        if (status == null){
            status = TaskStatus.NEW;
        }
        if (priority == null){
            priority = TaskPriority.MEDIUM;
        }
        if (creationDate == null){
            creationDate = LocalDateTime.now();
        }
    }

}
