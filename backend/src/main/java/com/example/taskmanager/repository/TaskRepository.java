package com.example.taskmanager.repository;

import com.example.taskmanager.model.Status;
import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {

    List<Task> findByStatus(Status status);

    Page<Task> findByStatus(Status status, Pageable pageable);

    Page<Task> findByStatusAndTitleContaining(Status status, String title, Pageable pageable);

    Page<Task> findByTitleContaining(String title, Pageable pageable);

    Page<Task> findByOwner(User owner, Pageable pageable);

}

//public interface TaskRepository extends JpaRepository<Task, Long>, JpaSpecificationExecutor<Task> {}