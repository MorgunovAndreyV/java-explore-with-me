package ru.practicum.subscription;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.comparators.SubscriptionComparators;
import ru.practicum.exception.SubscriptionValidationException;
import ru.practicum.friendship.FriendshipService;

import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping
@Slf4j
public class SubscriptionController {
    private final SubscriptionService subscriptionService;
    private final FriendshipService friendshipService;

    @PostMapping("/users/{userId}/subscriptions/{targetUserId}/subscribe")
    @ResponseStatus(HttpStatus.CREATED)
    public SubscriptionDto subscribe(@PathVariable Long userId,
                                     @PathVariable Long targetUserId) {

        if (!friendshipService.areFriends(userId, targetUserId)) {
            throw new SubscriptionValidationException("Нельзя подписаться на пользователя, " +
                    "не являющегося другом. Подписка пользователя с id:" + userId +
                    " на пользователя с id " + targetUserId + " не создана");
        }

        return SubscriptionMapper.toDto(subscriptionService.subscribe(userId, targetUserId));
    }


    @GetMapping("/users/{userId}/subscriptions")
    public List<SubscriptionDto> getUserSubscriptions(@PathVariable Long userId) {

        return subscriptionService.getUserSubscriptions(userId).stream()
                .sorted(SubscriptionComparators.compareEventsByUserid)
                .map(SubscriptionMapper::toDto).collect(Collectors.toList());
    }

    @DeleteMapping("/users/{userId}/subscriptions/{targetUserId}/unsubscribe")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void getOutcomingRequests(@PathVariable Long userId, @PathVariable Long targetUserId) {

        subscriptionService.unsubscribe(userId, targetUserId);
    }

}
