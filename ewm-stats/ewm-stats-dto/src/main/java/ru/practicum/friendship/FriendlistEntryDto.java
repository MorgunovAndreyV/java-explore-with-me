package ru.practicum.friendship;

import lombok.Builder;
import lombok.Data;
import ru.practicum.user.UserShortDto;

@Data
@Builder
public class FriendlistEntryDto {
    private UserShortDto user;
}
