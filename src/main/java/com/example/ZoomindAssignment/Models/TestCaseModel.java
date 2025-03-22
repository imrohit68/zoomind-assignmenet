package com.example.ZoomindAssignment.Models;

import com.example.ZoomindAssignment.Enums.Priority;
import com.example.ZoomindAssignment.Enums.Status;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document
@Getter
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndexes({
        @CompoundIndex(name = "status_priority_idx", def = "{'status': 1, 'priority': 1}"),
        @CompoundIndex(name = "priority_idx", def = "{'priority': 1}")
})
public class TestCaseModel {
    @Id
    private String id;

    @NotBlank(message = "Title cannot be null or empty")
    private String title;

    private String description;

    @NotNull(message = "Status cannot be null")
    private Status status;

    @NotNull(message = "Priority cannot be null")
    private Priority priority;

    @CreatedDate
    private Instant createdAt;

    @LastModifiedDate
    private Instant updatedAt;

    public TestCaseModel(String title, String description, Status status, Priority priority) {
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
    }

    public TestCaseModel(String id, String title, String description, Status status, Priority priority) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.status = status;
        this.priority = priority;
    }
}
