package com.example.taskmanager.controller;

import com.example.taskmanager.dto.*;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.service.TaskService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;


@RestController
@RequestMapping("/tasks")
public class TaskController {

    private final TaskService taskService;

    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }


    // --- CREATE TASK ---
    @PostMapping
    public TaskResponse createTask(@Valid @RequestBody TaskRequest request,Authentication authentication) {

        String username = authentication.getName();

        User user = taskService.userRepository
                .findByUsername(username)
                .orElseThrow();

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .status(request.getStatus())
                .owner(user)
                .build();

        Task saved = taskService.createTask(task);

        return mapToResponse(saved);
    }

    // --- GET TASK BY ID ---
    @GetMapping("/{id}")
    public ResponseEntity<TaskResponse> getTaskById(@PathVariable Long id) {

        return taskService.getTaskById(id)
                .map(task -> ResponseEntity.ok(mapToResponse(task)))
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<PagedResponse<TaskResponse>> getTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Boolean mine,
            Pageable pageable,
            Authentication authentication
    ) {

        // default sorting
        if (pageable.getSort().isUnsorted()) {
            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("createdAt").descending()
            );
        }

        Page<Task> taskPage;

        // ✅ فقط task های خود user
        if (Boolean.TRUE.equals(mine)) {

            String username = authentication.getName();

            User user = taskService.userRepository
                    .findByUsername(username)
                    .orElseThrow();

            taskPage = taskService.getTasksByOwner(user, pageable);

        } else {

            // ✅ همه task ها
            taskPage = taskService.getTasks(status, title, pageable);
        }

        // map -> DTO
        List<TaskResponse> content = taskPage.getContent().stream()
                .map(this::mapToResponse)
                .toList();

        // response
        PagedResponse<TaskResponse> response =
                PagedResponse.<TaskResponse>builder()
                        .data(content)
                        .page(taskPage.getNumber())
                        .size(taskPage.getSize())
                        .totalElements(taskPage.getTotalElements())
                        .totalPages(taskPage.getTotalPages())
                        .hasNext(taskPage.hasNext())
                        .hasPrevious(taskPage.hasPrevious())
                        .build();

        return ResponseEntity.ok(response);
    }

    // --- UPDATE TASK ---
    @PutMapping("/{id}")
    public ResponseEntity<TaskResponse> updateTask(
            @PathVariable Long id,
            @Valid @RequestBody TaskRequest request) {

        Task task = Task.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .build();

        Task updated = taskService.updateTask(id, task);

        return ResponseEntity.ok(mapToResponse(updated));
    }

    // --- UPDATE STATUS ---
    @PatchMapping("/{id}/status")
    public ResponseEntity<TaskResponse> updateStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {

        Task updated = taskService.updateTaskStatus(id, request.getStatus());

        return ResponseEntity.ok(mapToResponse(updated));
    }

    // --- DELETE TASK ---
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {

        taskService.deleteTask(id);

        return ResponseEntity.noContent().build();
    }

    // --- MAPPER (خیلی مهم برای تمیز بودن کد) ---
    private TaskResponse mapToResponse(Task task) {
        return TaskResponse.builder()
                .id(task.getId())
                .title(task.getTitle())
                .description(task.getDescription())
                .status(task.getStatus())
                .createdAt(task.getCreatedAt())
                .build();
    }


}