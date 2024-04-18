package ru.practicum.event;

import lombok.Builder;
import lombok.Data;
import ru.practicum.category.CategoryDto;
import ru.practicum.location.LocationDto;
import ru.practicum.user.UserShortDto;

@Data
@Builder
public class EventFullDto {
    private Long id;
    private String description;
    private String annotation;
    private String createdOn;
    private CategoryDto category;
    private Integer confirmedRequests;
    private String eventDate;
    private UserShortDto initiator;
    private Boolean paid;
    private String title;
    private Integer views;
    private String state;
    private LocationDto location;
    private String publishedOn;

    private Boolean requestModeration;
    private Integer participantLimit;

}
