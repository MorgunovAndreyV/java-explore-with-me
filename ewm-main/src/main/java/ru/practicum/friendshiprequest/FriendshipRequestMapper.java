package ru.practicum.friendshiprequest;

import ru.practicum.friendshiprequest.model.FriendshipRequest;

import java.time.format.DateTimeFormatter;

public class FriendshipRequestMapper {
    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static FriendshipRequestDto toDto(FriendshipRequest request) {
        return FriendshipRequestDto.builder()
                .id(request.getId())
                .sourceUserId(request.getSourceUserId())
                .targetUserId(request.getTargetUserId())
                .createdOn(request.getCreatedOn().format(dateTimeFormat))
                .status(request.getStatus().name())
                .build();
    }
}
