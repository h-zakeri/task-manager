package com.example.taskmanager.config;

import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.TaskRepository;
import com.example.taskmanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataSeeder {
    @Bean
    CommandLineRunner initUser(UserRepository repo, PasswordEncoder encoder) {
        return args -> {
            if (repo.findByUsername("admin").isEmpty()) {
                repo.save(new User("admin", encoder.encode("1234"), "ADMIN"));
                System.out.println("✅ Admin user created");
            }
        };
    }
    @Bean
    CommandLineRunner initDatabase(TaskRepository repository) {
        return args -> {

            if (repository.count() == 0) { // فقط اگر خالی بود

                repository.save(Task.builder()
                        .title("Learn Spring Boot")
                        .description("Study controllers and services")
                        .status(Status.TODO)
                        .build());

                repository.save(Task.builder()
                        .title("Practice Java")
                        .description("Solve exercises")
                        .status(Status.IN_PROGRESS)
                        .build());

                repository.save(Task.builder()
                        .title("Write Tests")
                        .description("Test APIs")
                        .status(Status.TODO)
                        .build());

                repository.save(Task.builder()
                        .title("Deploy Project")
                        .description("Prepare for production")
                        .status(Status.DONE)
                        .build());
            }
        };
    }
}