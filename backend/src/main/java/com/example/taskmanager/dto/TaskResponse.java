package com.example.taskmanager.dto;

import com.example.taskmanager.model.Status;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TaskResponse {

    private Long id;
    private String title;
    private String description;
    private Status status;
    private LocalDateTime createdAt;
}