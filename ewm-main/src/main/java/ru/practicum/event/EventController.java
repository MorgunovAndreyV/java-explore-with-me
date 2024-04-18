package ru.practicum.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EwmMainService;
import ru.practicum.event.model.State;
import ru.practicum.exception.EventValidationException;
import ru.practicum.stat.client.EndpointHitClient;
import ru.practicum.viewstat.ViewStatDto;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class EventController {
    private final EventService eventService;
    private final EndpointHitClient endpointHitClient;
    public static final String DEFAULT_START_DATE = "1970-01-01 00:00:00";
    public static final String DEFAULT_END_DATE = "2035-05-05 00:00:00";

    @GetMapping("/users/{userId}/events")
    public List<EventShortDto> getUserEventsPaginated(@PathVariable Long userId,
                                                      @RequestParam(defaultValue = "0", required = false) Integer from,
                                                      @RequestParam(defaultValue = "10", required = false) Integer size) {
        return eventService.getUserEventsPaginated(userId, from, size)
                .stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/users/{userId}/events/{eventId}")
    public EventFullDto getUserEventFull(@PathVariable Long userId,
                                         @PathVariable Long eventId) {
        return EventMapper.toFullDto(eventService.getUsersEventFull(userId, eventId));
    }

    @PostMapping("/users/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventFullDto addNew(@PathVariable Long userId, @RequestBody NewEventDto eventDto) {

        return EventMapper.toFullDto(eventService.addNew(userId, EventMapper.toEntity(eventDto)));
    }

    @PatchMapping("/users/{userId}/events/{eventId}")
    public EventFullDto changeEventData(@PathVariable Long userId,
                                        @PathVariable Long eventId,
                                        @RequestBody UpdateEventUserRequest eventDto) {

        return EventMapper.toFullDto(eventService.changeEventData(userId, eventId, eventDto));
    }

    @PatchMapping("/admin/events/{eventId}")
    public EventFullDto changeEventDataAdmin(@PathVariable(name = "eventId") Long eventId,
                                             @RequestBody UpdateEventAdminRequest eventDto) {

        return EventMapper.toFullDto(eventService.changeEventDataAdmin(eventId, eventDto));
    }

    @GetMapping("/admin/events")
    public List<EventFullDto> getAdminEventData(@Nullable @RequestParam(required = false) Long[] users,
                                                @Nullable @RequestParam(required = false) String[] states,
                                                @Nullable @RequestParam(required = false) Integer[] categories,
                                                @Nullable @RequestParam(required = false) String rangeStart,
                                                @Nullable @RequestParam(required = false) String rangeEnd,
                                                @Nullable @RequestParam(defaultValue = "0", required = false) Integer from,
                                                @Nullable @RequestParam(defaultValue = "10", required = false) Integer size) {
        return eventService.getAdminEventDataPaginated(users,
                states != null ? getStateArrayFromStringArray(states) : null,
                categories,
                rangeStart,
                rangeEnd,
                from,
                size).stream().map(EventMapper::toFullDto).collect(Collectors.toList());
    }

    @GetMapping("/events")
    public List<EventShortDto> getEvents(@Nullable @RequestParam(required = false) String text,
                                         @Nullable @RequestParam(required = false) Integer[] categories,
                                         @Nullable @RequestParam(required = false) Boolean paid,
                                         @Nullable @RequestParam(required = false) String rangeStart,
                                         @Nullable @RequestParam(required = false) String rangeEnd,
                                         @Nullable @RequestParam(required = false) Boolean onlyAvailable,
                                         @Nullable @RequestParam(required = false) String sort,
                                         @Nullable @RequestParam(defaultValue = "0", required = false) Integer from,
                                         @Nullable @RequestParam(defaultValue = "10", required = false) Integer size,
                                         HttpServletRequest request) {

        List<EventShortDto> eventsOut = eventService.getEventsPaginated(text, categories, paid, rangeStart, rangeEnd,
                        onlyAvailable, sort, from, size).stream()
                .map(EventMapper::toShortDto)
                .collect(Collectors.toList());


        ResponseEntity<List<ViewStatDto>> responseViewStatContents = endpointHitClient
                .getStatsExplicit(
                        rangeStart == null ? DEFAULT_START_DATE : rangeStart,
                        rangeEnd == null ? DEFAULT_END_DATE : rangeEnd,
                        eventsOut.stream()
                                .map(eventShortDto -> "/events/" + eventShortDto.getId()).collect(Collectors.toSet())
                                .toArray(new String[0]), false);

        if (responseViewStatContents.hasBody()) {
            List<ViewStatDto> viewStats = responseViewStatContents.getBody();

            if (viewStats != null) {
                eventsOut.forEach(eventShortDto -> {
                    ViewStatDto eventStat = viewStats.stream()
                            .filter(viewStatDto -> ("/events/" + eventShortDto.getId()).equals(viewStatDto.getUri()))
                            .findFirst().orElse(null);

                    if (eventStat == null) {
                        eventShortDto.setViews(0);
                    } else {
                        eventShortDto.setViews(eventStat.getHits());
                    }
                });
            }

        }

        endpointHitClient.registerHit(EwmMainService.APPTITLE, request);
        return eventsOut;
    }

    @GetMapping("/events/{id}")
    public EventFullDto getEventById(@PathVariable(name = "id") Long eventId,
                                     HttpServletRequest request) {
        endpointHitClient.registerHit(EwmMainService.APPTITLE, request);
        EventFullDto eventOut = EventMapper.toFullDto(eventService.getPublishedEventById(eventId));

        ResponseEntity<List<ViewStatDto>> responseContents = endpointHitClient
                .getStatsExplicit(DEFAULT_START_DATE, DEFAULT_END_DATE, new String[]{"/events/" + eventId},
                        true);


        if (responseContents.hasBody()) {
            List<ViewStatDto> viewStats = responseContents.getBody();

            if (viewStats == null || viewStats.isEmpty()) {
                eventOut.setViews(0);
            } else {
                eventOut.setViews(viewStats.get(0).getHits());
            }
        }

        return eventOut;
    }


    public static Set<State> getStateArrayFromStringArray(String[] stringArray) {
        return Arrays.stream(stringArray).map(str -> {
            State currentState;

            try {
                currentState = State.valueOf(str);

                return currentState;
            } catch (IllegalArgumentException e) {
                throw new EventValidationException("Передан неизвесный статус События: " + str);
            }

        }).collect(Collectors.toSet());

    }

}
