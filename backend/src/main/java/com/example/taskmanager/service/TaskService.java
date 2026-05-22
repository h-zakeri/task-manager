package com.example.taskmanager.service;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Optional;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    public final UserRepository userRepository;

    public TaskService(TaskRepository taskRepository,UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    /// ////////////////  Task List
    public List<Task> getAllTasks() {
        return taskRepository.findAll();
    }

    public Optional<Task> getTaskById(Long id) {
        return taskRepository.findById(id);
    }

    public List<Task> getTasksByStatus(Status status) {
        if (status != null) {
            return taskRepository.findByStatus(status);
        } else {
            return taskRepository.findAll();
        }
    }

    /// ///////// CRUD
    public Task createTask(Task task) {
        return taskRepository.save(task);
    }


    public Task updateTask(Long id, Task updatedTask) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());

        return taskRepository.save(task);
    }

    public Task updateTaskStatus(Long id, Status newStatus) {

        // 1️⃣ گرفتن Task از دیتابیس
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        // 2️⃣ چک کردن اگر Task قبلاً DONE شده
     //   if (task.getStatus() == Status.DONE) {
     //       throw new RuntimeException("Cannot change status of a completed task");
     //   }

        // 3️⃣ تغییر status
        task.setStatus(newStatus);
        // 4️⃣ ذخیره در دیتابیس
        return taskRepository.save(task);
    }

    public void deleteTask(Long id) {

        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Task not found"));

        taskRepository.delete(task);
    }

    /// ////////// PAGE
    public Page<Task> getTasksByStatus(Status status, Pageable pageable) {
        return taskRepository.findByStatus(status, pageable);
    }

    public Page<Task> getTasks(Pageable pageable) {
        return taskRepository.findAll(pageable);
    }

    public Page<Task> getTasks(Status status, String title,Pageable pageable) {

        boolean hasTitle = (title != null && !title.isBlank());
        System.out.println("TITLE: [" + title + "]");
        if(status != null && hasTitle){
            return taskRepository.findByStatusAndTitleContaining(status,title,pageable);
        }else if(status != null){
            return taskRepository.findByStatus(status,pageable);
        }else if(hasTitle){
            return taskRepository.findByTitleContaining(title, pageable);
        }
        return taskRepository.findAll(pageable);
    }

    public Page<Task> getTasksByOwner(User user, Pageable pageable) {
        return taskRepository.findByOwner(user,pageable);
    }
}