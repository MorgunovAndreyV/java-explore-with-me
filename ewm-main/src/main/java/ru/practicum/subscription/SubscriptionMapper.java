package ru.practicum.subscription;

import ru.practicum.subscription.model.Subscription;
import ru.practicum.user.UserMapper;

import java.time.format.DateTimeFormatter;

public class SubscriptionMapper {
    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static SubscriptionDto toDto(Subscription subscription) {
        return SubscriptionDto.builder()
                .targetUser(UserMapper.toShortDto(subscription.getTargetUser()))
                .createdOn(subscription.getCreatedOn().format(dateTimeFormat))
                .build();
    }
}
