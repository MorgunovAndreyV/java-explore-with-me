package ru.practicum.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class RequestController {
    private final RequestService requestService;

    @GetMapping("/users/{userId}/requests")
    public List<ParticipationRequestDto> getUserRequests(@PathVariable Long userId) {
        return requestService.getUserRequests(userId).stream().map(RequestMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> getUserEventRequests(@PathVariable Long userId,
                                                              @PathVariable Long eventId) {
        return requestService.getUserEventRequests(userId, eventId).stream().map(RequestMapper::toDto).collect(Collectors.toList());
    }

    @PostMapping("/users/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto addNewRequest(@PathVariable Long userId,
                                                 @RequestParam Long eventId) {
        return RequestMapper.toDto(requestService.addNewRequest(userId, eventId));
    }

    @PatchMapping("/users/{userId}/requests/{requestId}/cancel")
    public ParticipationRequestDto cancelUserRequest(@PathVariable Long userId,
                                                     @PathVariable Long requestId) {
        return RequestMapper.toDto(requestService.cancelUserRequest(userId, requestId));
    }

    @PatchMapping("/users/{userId}/events/{eventId}/requests")
    public EventRequestStatusUpdateResult changeEventUserRequest(@PathVariable Long userId,
                                                                 @PathVariable Long eventId,
                                                                 @RequestBody EventRequestStatusUpdateRequest updateRequest) {
        return requestService.changeUserRequestForEvent(userId, eventId, updateRequest);
    }


}
