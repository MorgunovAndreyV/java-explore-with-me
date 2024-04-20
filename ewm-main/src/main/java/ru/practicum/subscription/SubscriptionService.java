package ru.practicum.subscription;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.exception.SubscriptionValidationException;
import ru.practicum.subscription.model.QSubscription;
import ru.practicum.subscription.model.Subscription;
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
public class SubscriptionService {
    private final SubscriptionRepository subscriptionRepository;
    private final UserService userService;

    public Subscription subscribe(Long userId, Long targetUserId) {
        User user = userService.getUserById(userId);
        User targetUser = userService.getUserById(targetUserId);
        validateSubscriptionCreation(userId, targetUserId);

        Subscription newSubscription = Subscription.builder()
                .user(user)
                .targetUser(targetUser)
                .createdOn(LocalDateTime.now())
                .build();

        newSubscription = subscriptionRepository.save(newSubscription);

        log.info("Подписка пользователя id:" + userId + " на пользователя с id" + targetUserId + " добавлена");

        return newSubscription;
    }

    public void unsubscribe(Long userId, Long targetUserId) {
        Subscription subscription = validateSubscriptionDeletion(userId, targetUserId);

        subscriptionRepository.delete(subscription);

        log.info("Подписка пользователя id:" + userId + " на пользователя с id" + targetUserId + " удалена");

    }

    void validateSubscriptionCreation(Long userId, Long targetUserId) {
        BooleanBuilder booleanBuilder = new BooleanBuilder(
                QSubscription.subscription.user.id.eq(userId)
                        .and(QSubscription.subscription.targetUser.id.eq(targetUserId)));

        List<Subscription> userSubscriptions = StreamSupport.stream(
                        subscriptionRepository.findAll(booleanBuilder).spliterator(), false)
                .collect(Collectors.toList());

        if (!userSubscriptions.isEmpty()) {
            throw new SubscriptionValidationException("Подписка пользователя id:" + userId +
                    " на пользователя с id" + targetUserId + " уже существует");
        }

    }

    Subscription validateSubscriptionDeletion(Long userId, Long targetUserId) {
        userService.getUserById(userId);
        userService.getUserById(targetUserId);

        BooleanBuilder booleanBuilder = new BooleanBuilder(
                QSubscription.subscription.user.id.eq(userId)
                        .and(QSubscription.subscription.targetUser.id.eq(targetUserId)));

        List<Subscription> userSubscriptions = StreamSupport.stream(
                        subscriptionRepository.findAll(booleanBuilder).spliterator(), false)
                .collect(Collectors.toList());


        if (userSubscriptions.isEmpty()) {
            throw new SubscriptionValidationException("Подписки пользователя id:" + userId +
                    " на пользователя с id" + targetUserId + " не существует");
        }

        return userSubscriptions.get(0);

    }

    public List<Subscription> getUserSubscriptions(Long userId) {
        return getSubscriptions(userId, null);
    }

    public List<Subscription> getSubscriptions(Long sourceUserId, Long targetUserId) {

        if (sourceUserId != null || targetUserId != null) {
            BooleanBuilder booleanBuilder = new BooleanBuilder();

            if (sourceUserId != null) {
                booleanBuilder.and(QSubscription.subscription.user.id.eq(sourceUserId));
            }

            if (targetUserId != null) {
                booleanBuilder.and(QSubscription.subscription.targetUser.id.eq(targetUserId));
            }

            return StreamSupport.stream(
                            subscriptionRepository.findAll(booleanBuilder).spliterator(), false)
                    .collect(Collectors.toList());
        } else {

            return new ArrayList<>();
        }

    }

}
