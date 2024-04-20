package ru.practicum.friendship;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.friendship.model.Friendlist;
import ru.practicum.friendship.model.FriendlistEntry;
import ru.practicum.friendship.model.QFriendlist;
import ru.practicum.subscription.SubscriptionService;
import ru.practicum.user.UserService;
import ru.practicum.user.model.User;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class FriendshipService {
    private final FriendlistRepository friendlistRepository;
    private final FriendlistEntryRepository friendlistEntryRepository;
    private final UserService userService;
    private final SubscriptionService subscriptionService;


    public Friendlist getFriendlist(Long userId) {
        userService.getUserById(userId);

        List<Friendlist> userFriendLists = StreamSupport.stream(
                        friendlistRepository.findAll(QFriendlist.friendlist.userId.eq(userId)).spliterator(), false)
                .collect(Collectors.toList());

        Friendlist userFriendlist = null;
        if (userFriendLists.isEmpty()) {
            userFriendlist = Friendlist.builder()
                    .userId(userId)
                    .friends(new HashSet<>())
                    .build();
            userFriendlist = friendlistRepository.save(userFriendlist);
        } else {
            userFriendlist = userFriendLists.get(0);
        }

        return userFriendlist;
    }

    public void createFriendship(User sourceUser, User targetUser) {
        Friendlist sourceUserFriendlist = getFriendlist(sourceUser.getId());
        Friendlist targetUserFriendlist = getFriendlist(targetUser.getId());
        FriendlistEntry newSourceFriendlistEntry = FriendlistEntry.builder()
                .friendlistId(sourceUserFriendlist.getId())
                .user(targetUser)
                .createdOn(LocalDateTime.now())
                .build();
        FriendlistEntry newTargetFriendlistEntry = FriendlistEntry.builder()
                .friendlistId(targetUserFriendlist.getId())
                .user(sourceUser)
                .createdOn(LocalDateTime.now())
                .build();

        newSourceFriendlistEntry = friendlistEntryRepository.save(newSourceFriendlistEntry);
        newTargetFriendlistEntry = friendlistEntryRepository.save(newTargetFriendlistEntry);

        sourceUserFriendlist.addToFriendList(newSourceFriendlistEntry);
        targetUserFriendlist.addToFriendList(newTargetFriendlistEntry);

        friendlistRepository.save(sourceUserFriendlist);
        friendlistRepository.save(targetUserFriendlist);
    }

    public void deleteFriendship(Long sourceUserId, Long targetUserId) {
        userService.getUserById(sourceUserId);
        userService.getUserById(targetUserId);

        Friendlist sourceUserFriendlist = getFriendlist(sourceUserId);
        Friendlist targetUserFriendlist = getFriendlist(targetUserId);

        FriendlistEntry sourceFriendlistEntry = sourceUserFriendlist.getFriends().stream()
                .filter(friendlistEntry -> friendlistEntry.getUser().getId().equals(targetUserId))
                .findFirst().orElse(null);
        FriendlistEntry targetFriendlistEntry = targetUserFriendlist.getFriends().stream()
                .filter(friendlistEntry -> friendlistEntry.getUser().getId().equals(sourceUserId))
                .findFirst().orElse(null);


        if (targetFriendlistEntry != null) {
            friendlistEntryRepository.delete(targetFriendlistEntry);
            targetUserFriendlist.deleteFromFriendList(targetFriendlistEntry);
            friendlistRepository.save(targetUserFriendlist);
        }

        if (sourceFriendlistEntry != null) {
            friendlistEntryRepository.delete(sourceFriendlistEntry);
            sourceUserFriendlist.deleteFromFriendList(sourceFriendlistEntry);
            friendlistRepository.save(sourceUserFriendlist);
        }

        subscriptionService.unsubscribe(sourceUserId, targetUserId);
        subscriptionService.unsubscribe(targetUserId, sourceUserId);

    }

    public boolean areFriends(Long user1Id, Long user2Id) {
        Friendlist user1Friendlist = getFriendlist(user1Id);
        Friendlist user2Friendlist = getFriendlist(user2Id);

        FriendlistEntry flEntryFirstUser = user2Friendlist.getFriends().stream()
                .filter(friendlistEntry -> user1Id.equals(friendlistEntry.getUser().getId()))
                .findFirst().orElse(null);

        FriendlistEntry flEntrySecondUser = user1Friendlist.getFriends().stream()
                .filter(friendlistEntry -> user2Id.equals(friendlistEntry.getUser().getId()))
                .findFirst().orElse(null);

        return flEntryFirstUser != null && flEntrySecondUser != null;
    }

}
