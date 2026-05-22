package com.example.taskmanager;

import com.example.taskmanager.model.User;
import com.example.taskmanager.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TaskmanagerApplication {

	public static void main(String[] args) {
		SpringApplication.run(TaskmanagerApplication.class, args);
	}

	@Bean
	CommandLineRunner runner(UserRepository userRepository,PasswordEncoder encoder) {
		return args -> {

			if (userRepository.findByUsername("admin").isEmpty()) {

				User admin = new User();
				admin.setUsername("admin");
				admin.setPassword(encoder.encode("1234"));
				admin.setRole("ADMIN");

				userRepository.save(admin);

				System.out.println("Admin created");
			}
		};
	}
}
