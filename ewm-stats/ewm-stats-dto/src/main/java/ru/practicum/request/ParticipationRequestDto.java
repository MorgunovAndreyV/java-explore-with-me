package ru.practicum.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ParticipationRequestDto {

    private Long id;
    private Long event;
    private Long requester;
    private String created;
    private String status;

}
