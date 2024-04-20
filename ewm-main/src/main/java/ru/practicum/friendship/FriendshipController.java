package ru.practicum.friendship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping
@Slf4j
public class FriendshipController {
    public final FriendshipService friendshipService;

    @GetMapping("/users/{userId}/friendships")
    public FriendlistDto getUserFriendlistContents(@PathVariable Long userId) {

        return FriendshipMapper.toDto(friendshipService.getFriendlist(userId));
    }

    @DeleteMapping("/users/{userId}/friendships/{targetUserId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteFriendship(@PathVariable Long userId, @PathVariable Long targetUserId) {

        friendshipService.deleteFriendship(userId, targetUserId);
    }


}
