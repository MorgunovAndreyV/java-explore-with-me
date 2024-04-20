package ru.practicum.friendshiprequest;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.friendshiprequest.model.Status;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping
@Slf4j
public class FriendshipRequestController {
    public final FriendshipRequestService friendshipRequestService;

    @PostMapping("/users/{userId}/friendshiprequests")
    @ResponseStatus(HttpStatus.CREATED)
    public FriendshipRequestDto addNew(@PathVariable Long userId,
                                       @RequestParam Long targetUserId) {

        return FriendshipRequestMapper.toDto(friendshipRequestService.addNew(userId, targetUserId));
    }

    @PatchMapping("/users/{userId}/friendshiprequests/{sourceUserId}/confirm")
    public FriendshipRequestDto confirmFriendshipRequest(@PathVariable Long userId,
                                                         @PathVariable Long sourceUserId) {

        return FriendshipRequestMapper.toDto(friendshipRequestService.confirmRequest(userId, sourceUserId));
    }

    @PatchMapping("/users/{userId}/friendshiprequests/{sourceUserId}/decline")
    public FriendshipRequestDto declineFriendshipRequest(@PathVariable Long userId,
                                                         @PathVariable Long sourceUserId) {

        return FriendshipRequestMapper.toDto(friendshipRequestService.declineRequest(userId, sourceUserId));
    }

    @PatchMapping("/users/{userId}/friendshiprequests/{targetUserId}/cancel")
    public FriendshipRequestDto cancelFriendshipRequest(@PathVariable Long userId,
                                                        @PathVariable Long targetUserId) {

        return FriendshipRequestMapper.toDto(friendshipRequestService.cancelRequest(userId, targetUserId));
    }

    @GetMapping("/users/{userId}/friendshiprequests/incoming")
    public List<FriendshipRequestDto> getIncomingRequests(@PathVariable Long userId) {

        return friendshipRequestService
                .getRequests(null, userId, new Status[]{Status.WAITING}).stream()
                .map(FriendshipRequestMapper::toDto).collect(Collectors.toList());
    }

    @GetMapping("/users/{userId}/friendshiprequests/outcoming")
    public List<FriendshipRequestDto> getOutcomingRequests(@PathVariable Long userId) {

        return friendshipRequestService
                .getRequests(userId, null, new Status[]{Status.WAITING}).stream()
                .map(FriendshipRequestMapper::toDto).collect(Collectors.toList());
    }


}
