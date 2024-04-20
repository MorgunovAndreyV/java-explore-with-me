package ru.practicum.friendship.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "friendlists")
public class Friendlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "user_id", nullable = false)
    private Long userId;
    @OneToMany
    @JoinColumn(name = "friendlist_id")
    private Set<FriendlistEntry> friends = new HashSet<>();


    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
    }

    public Set<FriendlistEntry> getFriends() {
        return friends;
    }

    public FriendlistEntry addToFriendList(FriendlistEntry newFriendlistEntry) {
        friends.add(newFriendlistEntry);

        return newFriendlistEntry;
    }

    public void deleteFromFriendList(FriendlistEntry friendlistEntry) {
        friends.remove(friendlistEntry);
    }


}
