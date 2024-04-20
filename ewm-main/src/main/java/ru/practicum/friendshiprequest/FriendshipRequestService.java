package ru.practicum.friendshiprequest;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.FriendshipRequestValidationException;
import ru.practicum.friendship.FriendshipService;
import ru.practicum.friendship.model.Friendlist;
import ru.practicum.friendship.model.FriendlistEntry;
import ru.practicum.friendshiprequest.model.FriendshipRequest;
import ru.practicum.friendshiprequest.model.QFriendshipRequest;
import ru.practicum.friendshiprequest.model.Status;
import ru.practicum.user.UserService;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendshipRequestService {
    private final FriendshipService friendshipService;
    private final FriendshipRequestRepository friendshipRequestRepository;
    private final UserService userService;

    FriendshipRequest addNew(Long userId, Long targetUserId) {
        validateRequestCreation(userId, targetUserId);

        FriendshipRequest newRequest = FriendshipRequest.builder()
                .sourceUserId(userId)
                .targetUserId(targetUserId)
                .createdOn(LocalDateTime.now())
                .status(Status.WAITING)
                .build();

        newRequest = friendshipRequestRepository.save(newRequest);

        log.info("Запрос в друзья от пользователя id:" + userId + " пользователю id" + targetUserId + " добавлен");

        return newRequest;
    }

    FriendshipRequest confirmRequest(Long userId, Long sourceUserId) {
        User user = userService.getUserById(userId);
        User sourceUser = userService.getUserById(sourceUserId);

        FriendshipRequest request = validateRequestConfirmation(sourceUserId, userId);
        FriendshipRequest reversedRequest = null;

        request.confirmRequest();

        //найти реквест в другую сторону
        List<FriendshipRequest> reversedRequests = getRequests(userId, sourceUserId, new Status[]{Status.WAITING});

        request = friendshipRequestRepository.save(request);

        if (!reversedRequests.isEmpty()) {
            reversedRequest = reversedRequests.get(0);
            reversedRequest.confirmRequest();
            reversedRequest = friendshipRequestRepository.save(reversedRequest);
        }

        log.info("Запрос в друзья от пользователя id:" + sourceUserId + " пользователю id" + userId + " подтвержден");

        friendshipService.createFriendship(sourceUser, user);

        return request;
    }

    FriendshipRequest declineRequest(Long userId, Long sourceUserId) {
        userService.getUserById(userId);
        userService.getUserById(sourceUserId);

        FriendshipRequest request = validateRequestConfirmation(sourceUserId, userId);

        request.declineRequest();

        request = friendshipRequestRepository.save(request);


        log.info("Запрос в друзья от пользователя id:" + sourceUserId + " пользователю id" + userId + " отклонен");

        return request;
    }

    FriendshipRequest cancelRequest(Long userId, Long targetUserId) {
        userService.getUserById(userId);
        userService.getUserById(targetUserId);

        FriendshipRequest request = validateRequestConfirmation(userId, targetUserId);

        request.cancelRequest();

        request = friendshipRequestRepository.save(request);


        log.info("Запрос в друзья от пользователя id:" + userId + " пользователю id" + targetUserId + " отменен");

        return request;
    }

    void validateRequestCreation(Long userId, Long targetUserId) {
        userService.getUserById(userId);
        userService.getUserById(targetUserId);

        Friendlist sourceUserFriendlist = friendshipService.getFriendlist(userId);
        Friendlist targetUserFriendlist = friendshipService.getFriendlist(targetUserId);

        FriendlistEntry targetUserInSourceList = sourceUserFriendlist.getFriends().stream()
                .filter(friendlistEntry -> targetUserId.equals(friendlistEntry.getUser().getId()))
                .findFirst().orElse(null);

        FriendlistEntry sourceUserInTargetList = targetUserFriendlist.getFriends().stream()
                .filter(friendlistEntry -> userId.equals(friendlistEntry.getUser().getId()))
                .findFirst().orElse(null);

        if (targetUserInSourceList != null || sourceUserInTargetList != null) {
            throw new FriendshipRequestValidationException("Пользователь с id:" + userId + " уде есть в друзьях " +
                    "у пользователя id" + targetUserId);
        }

        if (!getRequests(userId, targetUserId, new Status[]{Status.WAITING}).isEmpty()) {
            throw new FriendshipRequestValidationException("В данный момент уже существует запрос на добавление в " +
                    "друзья от пользователя id:" + userId + " пользователю id" + targetUserId);
        }

    }

    FriendshipRequest validateRequestConfirmation(Long userId, Long sourceUserId) {
        userService.getUserById(userId);
        userService.getUserById(sourceUserId);

        List<FriendshipRequest> requests = getRequests(userId, sourceUserId, new Status[]{Status.WAITING});

        if (requests.isEmpty()) {
            throw new FriendshipRequestValidationException("Отсутствует запрос на добавление в " +
                    "друзья от пользователя id:" + sourceUserId + " пользователю id" + userId);
        }

        return requests.get(0);
    }

    List<FriendshipRequest> getRequests(Long sourceUserId, Long targetUserId, Status[] status) {

        if (sourceUserId != null || targetUserId != null) {
            BooleanBuilder booleanBuilder = new BooleanBuilder();

            if (sourceUserId != null) {
                booleanBuilder.and(QFriendshipRequest.friendshipRequest.sourceUserId.eq(sourceUserId));
            }

            if (targetUserId != null) {
                booleanBuilder.and(QFriendshipRequest.friendshipRequest.targetUserId.eq(targetUserId));
            }

            if (status != null) {
                booleanBuilder.and(QFriendshipRequest.friendshipRequest.status.in(status));
            }

            return StreamSupport.stream(
                            friendshipRequestRepository.findAll(booleanBuilder).spliterator(), false)
                    .collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }

    }


}
