package com.example.taskmanager.specification;

import com.example.taskmanager.model.Task;
import com.example.taskmanager.model.Status;
import org.springframework.data.jpa.domain.Specification;

public class TaskSpecification {

    public static Specification<Task> hasStatus(Status status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    public static Specification<Task> titleContains(String title) {
        return (root, query, cb) ->
                (title == null || title.isBlank())
                        ? null
                        : cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }
}