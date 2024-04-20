package ru.practicum.subscription;

import lombok.Builder;
import lombok.Data;
import ru.practicum.user.UserShortDto;

@Builder
@Data
public class SubscriptionDto {
    private UserShortDto targetUser;
    private String createdOn;

}
