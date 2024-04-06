package ru.practicum.endpointhit;

import lombok.*;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EndpointHitDto {
    private String app;
    private String uri;
    private String ip;
    private LocalDateTime hitTimestamp;
}
