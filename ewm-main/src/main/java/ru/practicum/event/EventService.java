package ru.practicum.event;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.CategoryService;
import ru.practicum.comparators.EventComparators;
import ru.practicum.event.model.*;
import ru.practicum.exception.EventValidationException;
import ru.practicum.exception.RecordNotFoundException;
import ru.practicum.location.LocationService;
import ru.practicum.user.UserService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;
    private final UserService userService;
    private final CategoryService categoryService;
    private final LocationService locationService;
    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Event saveEvent(Event event) {
        return eventRepository.save(event);
    }

    public Event addNew(Long userId, Event event) {
        validateEventDataCreation(event);

        event.setCategory(categoryService.getCategoryById(event.getCategory().getId()));
        event.setInitiator(userService.getUserById(userId));
        event.setLocation(locationService.addNew(event.getLocation()));
        event.setCreatedOn(LocalDateTime.now());
        event.setState(State.WAITING);
        event.setConfirmedRequests(0);
        event.setViews(0);

        if (!event.getRequestModeration()) {
            event.sendToReview();
        }

        Event newEvent = eventRepository.save(event);

        log.info("Новое событие добавлено успешно. id:" + event.getId());

        return newEvent;
    }

    public Event changeEventData(Long userId, Long eventId, UpdateEventUserRequest eventDto)
            throws RecordNotFoundException {
        Event eventFromBase = getUsersEventFull(userId, eventId);

        validateEventDataChange(eventFromBase, eventDto);
        EventMapper.fillFromDto(eventDto, eventFromBase);

        if (eventDto.getStateAction() != null) {
            if (userId.equals(eventFromBase.getInitiator().getId())) {
                if (StateAction.SEND_TO_REVIEW.equals(getStateAction(eventDto.getStateAction()))) {
                    eventFromBase.sendToReview();

                } else if (StateAction.CANCEL_REVIEW.equals(getStateAction(eventDto.getStateAction()))) {
                    eventFromBase.cancelReview();

                }

            }

        }

        if (eventDto.getCategory() != null) {
            eventFromBase.setCategory(categoryService.getCategoryById(eventDto.getCategory()));
        }

        log.info("Запись события изменена успешно. id:" + eventFromBase.getId());

        return eventRepository.save(eventFromBase);
    }

    public Event changeEventDataAdmin(Long eventId, UpdateEventAdminRequest eventDto)
            throws RecordNotFoundException {
        Event eventFromBase = getEventById(eventId);

        validateEventDataChangeAdmin(eventFromBase, eventDto);

        if (eventDto.getStateAction() != null) {
            if (StateAction.PUBLISH_EVENT.equals(getStateAction(eventDto.getStateAction()))) {
                eventFromBase.publishEvent();

            } else if (StateAction.REJECT_EVENT.equals(getStateAction(eventDto.getStateAction()))) {
                eventFromBase.cancelEvent();

            }
        }

        EventMapper.fillFromDto(eventDto, eventFromBase);

        eventFromBase.setLocation(locationService.addNew(eventFromBase.getLocation()));

        if (eventDto.getCategory() != null) {
            eventFromBase.setCategory(categoryService.getCategoryById(eventDto.getCategory()));
        }

        eventRepository.save(eventFromBase);

        log.info("Запись категории изменена успешно. id:" + eventFromBase.getId());


        return eventFromBase;
    }

    public Event getEventById(Long eventId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder();
        booleanBuilder.and(QEvent.event.id.eq(eventId));

        List<Event> eventsFromBase = StreamSupport.stream(eventRepository.findAll(booleanBuilder).spliterator(), false)
                .collect(Collectors.toList());

        if (eventsFromBase.isEmpty()) {
            throw new RecordNotFoundException("Событие с id " + eventId + " не найдено");
        }

        //получение количества просмотров события
        //получение подтвержденных запросов

        return eventsFromBase.get(0);
    }

    public Event getPublishedEventById(Long eventId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder(QEvent.event.state.eq(State.PUBLISHED));
        booleanBuilder.and(QEvent.event.id.eq(eventId));

        List<Event> eventsFromBase = StreamSupport.stream(eventRepository.findAll(booleanBuilder).spliterator(), false)
                .collect(Collectors.toList());

        if (eventsFromBase.isEmpty()) {
            throw new RecordNotFoundException("Событие с id " + eventId + " не найдено");
        }

        //получение количества просмотров события
        //получение подтвержденных запросов

        return eventsFromBase.get(0);
    }

    public Event getUsersEventFull(Long userId, Long eventId) {
        userService.getUserById(userId);

        Event eventFromBase = eventRepository.findByUserIdAndEventId(userId, eventId);

        if (eventFromBase == null) {
            throw new RecordNotFoundException("Событие с id " + eventId + "для пользователя " +
                    userId + " не найдено.");
        }
        return eventFromBase;
    }

    public List<Event> getUserEventsPaginated(Long userId, Integer from, Integer size) {
        PageRequest pageRequest;

        if (size != null && from != null) {
            pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
            return eventRepository.findByUserId(userId, pageRequest).getContent();

        } else {
            return eventRepository.findByUserId(userId);
        }

    }

    public List<Event> getAdminEventDataPaginated(Long[] userIds, Set<State> states, Integer[] categories, String rangeStart,
                                                  String rangeEnd, Integer from, Integer size) {
        PageRequest pageRequest;
        BooleanBuilder bd = new BooleanBuilder();

        if (userIds != null) {
            bd.and(QEvent.event.initiator.id.in(Set.of(userIds)));
        }

        if (states != null) {
            bd.and(QEvent.event.state.in(states));
        }

        if (categories != null) {
            bd.and(QEvent.event.category.id.in(categories));
        }

        if (rangeStart != null) {
            bd.and(QEvent.event.eventDate.after(LocalDateTime.parse(rangeStart, dateTimeFormat)));
        }

        if (rangeEnd != null) {
            bd.and(QEvent.event.eventDate.before(LocalDateTime.parse(rangeEnd, dateTimeFormat)));
        }

        if (size != null && from != null) {
            pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);

            return eventRepository.findAll(bd, pageRequest).getContent();

        }

        return StreamSupport.stream(eventRepository.findAll(bd).spliterator(), false)
                .collect(Collectors.toList());

    }

    public List<Event> getEventsPaginated(String searchText, Integer[] categories, Boolean paid, String rangeStart,
                                          String rangeEnd, Boolean onlyAvailable, String sortType,
                                          Integer from, Integer size) {
        List<Event> resultList;
        PageRequest pageRequest;
        BooleanBuilder bd = new BooleanBuilder(QEvent.event.state.eq(State.PUBLISHED));

        if (searchText != null) {
            bd.and(QEvent.event.annotation.containsIgnoreCase(searchText)
                    .or(QEvent.event.description.containsIgnoreCase(searchText)));
        }

        if (categories != null) {
            bd.and(QEvent.event.category.id.in(categories));
        }

        if (paid != null) {
            bd.and(QEvent.event.paid.eq(paid));
        }

        if (onlyAvailable != null) {
            bd.and(QEvent.event.confirmedRequests.lt(QEvent.event.participantLimit));
        }

        if (rangeStart != null) {
            bd.and(QEvent.event.eventDate.after(LocalDateTime.parse(rangeStart, dateTimeFormat)));
        }

        if (rangeEnd != null) {
            bd.and(QEvent.event.eventDate.before(LocalDateTime.parse(rangeEnd, dateTimeFormat)));
        }

        if (rangeStart == null && rangeEnd == null) {
            bd.and(QEvent.event.eventDate.after(LocalDateTime.now()));
        }

        if (rangeEnd != null && rangeStart != null) {
            if (LocalDateTime.parse(rangeStart, dateTimeFormat).isAfter(LocalDateTime.parse(rangeEnd, dateTimeFormat))) {
                throw new EventValidationException("Начальная дата диаппазона фильтрации должна быть перед конечной датой");
            }
        }

        if (size != null && from != null) {
            pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
            resultList = eventRepository.findAll(bd, pageRequest).getContent();

        } else {
            resultList = StreamSupport.stream(eventRepository.findAll(bd).spliterator(), false)
                    .collect(Collectors.toList());

        }

        if (sortType != null) {
            try {
                if (SortType.EVENT_DATE.equals(SortType.valueOf(sortType))) {
                    return resultList.stream()
                            .sorted(EventComparators.compareEventsByEventDate).collect(Collectors.toList());
                } else if (SortType.VIEWS.equals(SortType.valueOf(sortType))) {
                    return resultList.stream()
                            .sorted(EventComparators.compareEventsByViews).collect(Collectors.toList());
                }
            } catch (IllegalArgumentException e) {
                return resultList;
            }

        }

        return resultList;
    }

    StateAction getStateAction(String action) {
        try {
            return StateAction.valueOf(action);
        } catch (IllegalArgumentException e) {

            throw new EventValidationException("Невозможно определить действие над событием.");
        }
    }

    public List<Event> getEventsByCategory(Long catId) {
        return StreamSupport.stream(eventRepository.findAll(QEvent.event.category.id.eq(catId)).spliterator(), false)
                .collect(Collectors.toList());
    }

    void validateEventDataCreation(Event event) {
        if (event.getPaid() == null) {
            event.setPaid(false);
        }

        if (event.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }

        if (event.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }

        if (event.getParticipantLimit() < 0) {
            throw new EventValidationException("Ожидаемое число участников не может быть отрицательным");
        }

        if (event.getAnnotation() == null || event.getAnnotation().isEmpty() || event.getAnnotation().trim().isEmpty()) {
            throw new EventValidationException("Аннотация не может быть пустой");
        } else if (!(event.getAnnotation().length() >= 20 && event.getAnnotation().length() <= 2000)) {
            throw new EventValidationException("Некорректная длина аннотации");
        }

        if (event.getTitle() == null || event.getTitle().isEmpty()
                || event.getTitle().trim().isEmpty()) {
            throw new EventValidationException("Заголовок не может быть пустым");
        } else if (!(event.getTitle().length() >= 3 && event.getTitle().length() <= 120)) {
            throw new EventValidationException("Некорректная длина заголовка");
        }

        if (event.getLocation() == null) {
            throw new EventValidationException("Локация не может быть пустой");
        }

        if (event.getDescription() == null || event.getDescription().isEmpty()
                || event.getDescription().trim().isEmpty()) {
            throw new EventValidationException("Описание не может быть пустым");
        } else if (!(event.getDescription().length() >= 20 && event.getDescription().length() <= 7000)) {
            throw new EventValidationException("Некорректная длина заголовка");
        }

        if (event.getCategory() == null || event.getCategory().getId() == null) {
            throw new EventValidationException("Категория не может быть пустой");
        }

        if (event.getEventDate() == null) {
            throw new EventValidationException("Дата события не может быть пустой");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now())) {
            throw new EventValidationException("Переданная дата начала события уже наступила");
        }

        if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
            throw new DataIntegrityViolationException("Событие нельзя назначить за 2 часа до его начала");
        }
    }

    void validateEventDataChange(Event event, UpdateEventUserRequest eventDto) {
        if (State.PUBLISHED.equals(event.getState())) {
            throw new DataIntegrityViolationException("Допустимо изменять только отмененные или события, " +
                    "ожидающие модерации");
        }

        if (eventDto.getEventDate() != null) {
            if (LocalDateTime.parse(eventDto.getEventDate(), dateTimeFormat).isBefore(LocalDateTime.now())) {
                throw new EventValidationException("Переданная дата начала события уже наступила");
            }

            if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(2L))) {
                throw new DataIntegrityViolationException("События нельзя изменять за два часа до их начала");
            }

        }

        if (eventDto.getParticipantLimit() != null && eventDto.getParticipantLimit() < 0) {
            throw new EventValidationException("Ожидаемое число участников не может быть отрицательным");
        }

        if (eventDto.getAnnotation() != null) {
            if (eventDto.getAnnotation().isEmpty() || eventDto.getAnnotation().trim().isEmpty()) {
                throw new EventValidationException("Аннотация не может быть пустой");
            } else if (!(eventDto.getAnnotation().length() >= 20 && eventDto.getAnnotation().length() <= 2000)) {
                throw new EventValidationException("Некорректная длина аннотации");
            }
        }

        if (eventDto.getTitle() != null) {
            if ((eventDto.getTitle().isEmpty() || eventDto.getTitle().trim().isEmpty())) {
                throw new EventValidationException("Заголовок не может быть пустым");
            } else if (!(eventDto.getTitle().length() >= 3 && eventDto.getTitle().length() <= 120)) {
                throw new EventValidationException("Некорректная длина заголовка");
            }
        }

        if (eventDto.getDescription() != null) {
            if ((eventDto.getDescription().isEmpty()
                    || eventDto.getDescription().trim().isEmpty())) {
                throw new EventValidationException("Описание не может быть пустым");
            } else if (!(eventDto.getDescription().length() >= 20 && eventDto.getDescription().length() <= 7000)) {
                throw new EventValidationException("Некорректная длина заголовка");
            }
        }

    }

    void validateEventDataChangeAdmin(Event event, UpdateEventAdminRequest eventDto) {
        if (eventDto.getEventDate() != null) {
            if (LocalDateTime.parse(eventDto.getEventDate(), dateTimeFormat).isBefore(LocalDateTime.now())) {
                throw new EventValidationException("Переданная дата начала события уже наступила");
            }

            if (event.getEventDate().isBefore(LocalDateTime.now().plusHours(1L))) {
                throw new DataIntegrityViolationException("События нельзя изменять за час до их начала");
            }
        }

        if (eventDto.getParticipantLimit() != null && eventDto.getParticipantLimit() < 0) {
            throw new EventValidationException("Ожидаемое число участников не может быть отрицательным");
        }

        if (eventDto.getAnnotation() != null) {
            if (eventDto.getAnnotation().isEmpty() || eventDto.getAnnotation().trim().isEmpty()) {
                throw new EventValidationException("Аннотация не может быть пустой");
            } else if (!(eventDto.getAnnotation().length() >= 20 && eventDto.getAnnotation().length() <= 2000)) {
                throw new EventValidationException("Некорректная длина аннотации");
            }
        }

        if (eventDto.getTitle() != null) {
            if ((eventDto.getTitle().isEmpty() || eventDto.getTitle().trim().isEmpty())) {
                throw new EventValidationException("Заголовок не может быть пустым");
            } else if (!(eventDto.getTitle().length() >= 3 && eventDto.getTitle().length() <= 120)) {
                throw new EventValidationException("Некорректная длина заголовка");
            }
        }

        if (eventDto.getDescription() != null) {
            if ((eventDto.getDescription().isEmpty()
                    || eventDto.getDescription().trim().isEmpty())) {
                throw new EventValidationException("Описание не может быть пустым");
            } else if (!(eventDto.getDescription().length() >= 20 && eventDto.getDescription().length() <= 7000)) {
                throw new EventValidationException("Некорректная длина заголовка");
            }
        }

    }

}