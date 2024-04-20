package ru.practicum.friendshiprequest;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FriendshipRequestDto {
    private Long id;
    private Long sourceUserId;
    private Long targetUserId;
    private String status;
    private String createdOn;
}
