package ru.practicum.request;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import ru.practicum.event.EventService;
import ru.practicum.event.model.Event;
import ru.practicum.exception.EventValidationException;
import ru.practicum.exception.RecordNotFoundException;
import ru.practicum.request.model.QRequest;
import ru.practicum.request.model.Request;
import ru.practicum.request.model.State;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestService {
    private final RequestRepository requestRepository;
    private final EventService eventService;

    List<Request> getUserRequests(Long userId) {
        return StreamSupport.stream(requestRepository.findAll(QRequest.request.requesterId.eq(userId)).spliterator(), false)
                .collect(Collectors.toList());
    }

    Request addNewRequest(Long userId, Long eventId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder(QRequest.request.requesterId.eq(userId)
                .and(QRequest.request.eventId.eq(eventId)));
        List<Request> sameRequests = StreamSupport.stream(
                        requestRepository.findAll(booleanBuilder).spliterator(), false)
                .collect(Collectors.toList());

        if (!sameRequests.isEmpty()) {
            throw new DataIntegrityViolationException("Нельзя повторно добавить заявку на участие " +
                    "для пользователя: userId:" + userId + ", eventId:" + eventId);
        }

        Event event = eventService.getEventById(eventId);

        if (userId.equals(event.getInitiator().getId())) {
            throw new DataIntegrityViolationException("Нельзя подавать заявку на участие в собственном событии: " +
                    "userId:" + userId + ", eventId:" + eventId);
        }

        if (!ru.practicum.event.model.State.PUBLISHED.equals(event.getState())) {
            throw new DataIntegrityViolationException("Нельзя подавать заявку на участие в неопубликованном событии: " +
                    "userId:" + userId + ", eventId:" + eventId + ", eventState:" + event.getState());
        }

        if (event.getParticipantLimit() != 0 && (getEventConfirmedRequestCount(eventId) >= event.getParticipantLimit())) {
            throw new DataIntegrityViolationException("У события превышен лимит подтвержденных участников: " +
                    "eventId:" + eventId + ", eventPartLimit:" + event.getParticipantLimit());
        }

        Request newRequest = Request.builder()
                .eventId(eventId)
                .requesterId(userId)
                .state(State.PENDING)
                .createdDate(LocalDateTime.now())
                .build();

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            newRequest.setState(State.CONFIRMED);
            event.setConfirmedRequests(getEventConfirmedRequestCount(eventId));
            eventService.saveEvent(event);
        }

        newRequest = requestRepository.save(newRequest);

        return newRequest;
    }

    Request cancelUserRequest(Long userId, Long requestId) {
        List<Request> responseList = StreamSupport.stream(requestRepository
                        .findAll(QRequest.request.requesterId.eq(userId).and(QRequest.request.id.eq(requestId)))
                        .spliterator(), false)
                .collect(Collectors.toList());

        if (responseList.isEmpty()) {
            throw new RecordNotFoundException("Заявок на участие с id " + requestId + " от пользователя " + userId +
                    " не найдено.");
        }

        Request requestFromBase = responseList.get(0);
        requestFromBase.setState(State.CANCELED);

        Event relatedEvent = eventService.getEventById(requestFromBase.getEventId());
        requestRepository.save(requestFromBase);

        relatedEvent.setConfirmedRequests(getEventConfirmedRequestCount(requestFromBase.getEventId()));
        eventService.saveEvent(relatedEvent);

        return requestFromBase;
    }

    EventRequestStatusUpdateResult changeUserRequestForEvent(Long userId, Long eventId,
                                                             EventRequestStatusUpdateRequest updateRequest) {
        Event userEvent = eventService.getUsersEventFull(userId, eventId);

        if (userEvent == null) {
            throw new EventValidationException("Пользователь не является инициатором данного события: userId:" + userId +
                    " eventId:" + eventId);
        }

        validateEventStatusUpdateRequest(updateRequest);

        State newState = State.valueOf(updateRequest.getStatus());
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();

        for (Long id : updateRequest.getRequestIds()) {
            if (userEvent.getParticipantLimit() > getEventConfirmedRequestCount(eventId)) {
                Request request = requestRepository.findById(id).orElse(null);

                if (request == null) {
                    throw new RecordNotFoundException("Заявка на участие с id " + id + " не найдена");
                }

                if (State.CONFIRMED.equals(newState)) {
                    request.confirmRequest();
                    result.addToConfirmed(RequestMapper.toDto(request));
                }

                if (State.REJECTED.equals(newState)) {
                    request.rejectRequest();
                    result.addToRejected(RequestMapper.toDto(request));
                }

                requestRepository.save(request);
                userEvent.setConfirmedRequests(getEventConfirmedRequestCount(userEvent.getId()));
                eventService.saveEvent(userEvent);

            } else {
                throw new DataIntegrityViolationException("Для события id:" + eventId + " превышен лимит подтвержденных " +
                        "заявок");
            }

        }

        return result;
    }

    List<Request> getUserEventRequests(Long userId, Long eventId) {
        Event userEvent = eventService.getUsersEventFull(userId, eventId);

        if (userEvent == null) {
            throw new EventValidationException("Пользователь не является инициатором данного события: userId:" + userId +
                    " eventId:" + eventId);
        }

        BooleanBuilder booleanBuilder = new BooleanBuilder(QRequest.request.eventId.eq(eventId));

        return StreamSupport.stream(
                        requestRepository.findAll(booleanBuilder).spliterator(), false)
                .collect(Collectors.toList());
    }


    Integer getEventConfirmedRequestCount(Long eventId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder(QRequest.request.eventId.eq(eventId)
                .and(QRequest.request.state.eq(State.CONFIRMED)));
        List<Request> requestForEvent = StreamSupport.stream(
                        requestRepository.findAll(booleanBuilder).spliterator(), false)
                .collect(Collectors.toList());

        return requestForEvent.size();
    }

    void validateEventStatusUpdateRequest(EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        try {
            State.valueOf(eventRequestStatusUpdateRequest.getStatus());
        } catch (IllegalArgumentException e) {
            throw new DataIntegrityViolationException("Статуса " + eventRequestStatusUpdateRequest.getStatus() +
                    " не существует.");
        }

    }

}
