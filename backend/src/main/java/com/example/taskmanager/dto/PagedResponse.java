package com.example.taskmanager.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

@Data
@Builder
public class PagedResponse<T> {

    private List<T> data;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
    private boolean hasPrevious;
}