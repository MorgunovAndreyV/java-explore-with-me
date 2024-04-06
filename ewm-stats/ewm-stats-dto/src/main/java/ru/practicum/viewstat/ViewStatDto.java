package ru.practicum.viewstat;

import lombok.*;

@Data
@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ViewStatDto {
    private String app;
    private String uri;
    private Integer hits;

}
