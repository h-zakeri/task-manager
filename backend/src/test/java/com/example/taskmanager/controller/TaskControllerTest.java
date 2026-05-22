package com.example.taskmanager.controller;

import com.example.taskmanager.dto.UpdateStatusRequest;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.repository.TaskRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class TaskControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Task task;

    @BeforeEach
    public void setup() {
        taskRepository.deleteAll();
        task = Task.builder()
                .title("Test Task")
                .description("Integration Test")
                .status(Status.TODO)
                .build();
        task = taskRepository.save(task);
    }

    // --- Test 1: valid status change ---
    @Test
    public void testUpdateStatus_Valid() throws Exception {
        UpdateStatusRequest request = new UpdateStatusRequest(Status.IN_PROGRESS);

        mockMvc.perform(patch("/tasks/{id}/status", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    }

    // --- Test 2: cannot change DONE ---
    @Test
    public void testUpdateStatus_CannotChangeDone() throws Exception {
        task.setStatus(Status.DONE);
        taskRepository.save(task);

        UpdateStatusRequest request = new UpdateStatusRequest(Status.TODO);

        mockMvc.perform(patch("/tasks/{id}/status", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value("Cannot change status of a completed task"));
    }

    // --- Test 3: validation error (null status) ---
    @Test
    public void testUpdateStatus_ValidationError() throws Exception {
        UpdateStatusRequest request = new UpdateStatusRequest(null);

        mockMvc.perform(patch("/tasks/{id}/status", task.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.status").value("Status cannot be null"));
    }
}