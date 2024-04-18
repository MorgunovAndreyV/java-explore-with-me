package ru.practicum.event;

import lombok.experimental.UtilityClass;
import ru.practicum.category.CategoryMapper;
import ru.practicum.category.model.Category;
import ru.practicum.event.model.Event;
import ru.practicum.location.LocationMapper;
import ru.practicum.user.UserMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@UtilityClass
public class EventMapper {
    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public static Event toEntity(NewEventDto eventDto) {
        return Event.builder()
                .annotation(eventDto.getAnnotation())
                .category(new Category(eventDto.getCategory(), null))
                .description(eventDto.getDescription())
                .eventDate(LocalDateTime.parse(eventDto.getEventDate(), dateTimeFormat))
                .location(LocationMapper.toEntity(eventDto.getLocation()))
                .paid(eventDto.getPaid())
                .participantLimit(eventDto.getParticipantLimit())
                .requestModeration(eventDto.getRequestModeration())
                .title(eventDto.getTitle())
                .build();
    }

    public static EventFullDto toFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .state(event.getState().toString())
                .createdOn(event.getCreatedOn() != null ? event.getCreatedOn().format(dateTimeFormat) : null)
                .publishedOn(event.getPublishedOn() != null ? event.getPublishedOn().format(dateTimeFormat) : null)
                .location(LocationMapper.toDto(event.getLocation()))
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .annotation(event.getAnnotation())
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .eventDate(event.getEventDate() != null ? event.getEventDate().format(dateTimeFormat) : null)
                .category(CategoryMapper.toDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .description(event.getDescription())
                .requestModeration(event.getRequestModeration())
                .participantLimit(event.getParticipantLimit())
                .build();
    }

    public static EventShortDto toShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .initiator(UserMapper.toShortDto(event.getInitiator()))
                .annotation(event.getAnnotation())
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .eventDate(event.getEventDate() != null ? event.getEventDate().format(dateTimeFormat) : null)
                .category(CategoryMapper.toDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .description(event.getDescription())
                .build();
    }

    public static void fillFromDto(UpdateEventUserRequest eventDto, Event event) {
        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }

        if (eventDto.getCategory() != null) {
            event.setCategory(new Category(event.getId(), null));
        }

        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }

        if (eventDto.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(eventDto.getEventDate(), dateTimeFormat));
        }

        if (eventDto.getLocation() != null) {
            event.setLocation(LocationMapper.toEntity(eventDto.getLocation()));
        }

        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }

        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }

        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }

        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }

    }

    public static void fillFromDto(UpdateEventAdminRequest eventDto, Event event) {
        if (eventDto.getAnnotation() != null) {
            event.setAnnotation(eventDto.getAnnotation());
        }

        if (eventDto.getCategory() != null) {
            event.setCategory(new Category(event.getId(), null));
        }

        if (eventDto.getDescription() != null) {
            event.setDescription(eventDto.getDescription());
        }

        if (eventDto.getEventDate() != null) {
            event.setEventDate(LocalDateTime.parse(eventDto.getEventDate(), dateTimeFormat));
        }

        if (eventDto.getLocation() != null) {
            event.setLocation(LocationMapper.toEntity(eventDto.getLocation()));
        }

        if (eventDto.getPaid() != null) {
            event.setPaid(eventDto.getPaid());
        }

        if (eventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventDto.getParticipantLimit());
        }

        if (eventDto.getRequestModeration() != null) {
            event.setRequestModeration(eventDto.getRequestModeration());
        }

        if (eventDto.getTitle() != null) {
            event.setTitle(eventDto.getTitle());
        }

    }

}