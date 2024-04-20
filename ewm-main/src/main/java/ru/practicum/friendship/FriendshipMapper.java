package ru.practicum.friendship;

import ru.practicum.friendship.model.Friendlist;
import ru.practicum.friendship.model.FriendlistEntry;
import ru.practicum.user.UserMapper;

import java.util.stream.Collectors;

public class FriendshipMapper {
    public static FriendlistDto toDto(Friendlist friendlist) {
        return FriendlistDto.builder()
                .friendlistEntries(friendlist.getFriends().stream().map(FriendshipMapper::toDto)
                        .collect(Collectors.toSet()))
                .build();
    }

    public static FriendlistEntryDto toDto(FriendlistEntry friendlistEntry) {
        return FriendlistEntryDto.builder()
                .user(UserMapper.toShortDto(friendlistEntry.getUser()))
                .build();
    }

}
