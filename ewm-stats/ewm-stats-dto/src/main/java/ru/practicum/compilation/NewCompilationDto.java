package ru.practicum.compilation;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class NewCompilationDto {
    private String title;
    private Set<Long> events;
    private Boolean pinned;
}
