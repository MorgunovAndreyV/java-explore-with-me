package ru.practicum.event;

import lombok.Builder;
import lombok.Data;
import ru.practicum.location.LocationDto;

@Builder
@Data
public class UpdateEventAdminRequest {
    private String annotation;
    private Long category;
    private String description;
    private String eventDate;
    private LocationDto location;
    private Boolean paid;
    private Integer participantLimit;
    private Boolean requestModeration;
    private String stateAction;
    private String title;
}
