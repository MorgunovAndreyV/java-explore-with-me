package ru.practicum.friendship;

import lombok.Builder;
import lombok.Data;

import java.util.Set;

@Data
@Builder
public class FriendlistDto {
    private Set<FriendlistEntryDto> friendlistEntries;
}
