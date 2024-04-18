package ru.practicum.request;

import lombok.experimental.UtilityClass;
import ru.practicum.request.model.Request;

import java.time.format.DateTimeFormatter;

@UtilityClass
public class RequestMapper {
    public static DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static ParticipationRequestDto toDto(Request request) {
        return ParticipationRequestDto.builder()
                .id(request.getId())
                .event(request.getEventId())
                .requester(request.getRequesterId())
                .status(request.getState().toString())
                .created(request.getCreatedDate() != null ? request.getCreatedDate().format(dateTimeFormat) : null)
                .build();
    }
}
