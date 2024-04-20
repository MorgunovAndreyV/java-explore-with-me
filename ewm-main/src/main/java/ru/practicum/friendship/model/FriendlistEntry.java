package ru.practicum.friendship.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "friendlistentries")
public class FriendlistEntry {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "friendlist_id", nullable = false)
    private Long friendlistId;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;


    public Long getId() {
        return id;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public Long getFriendlistId() {
        return friendlistId;
    }

    public User getUser() {
        return user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendlistEntry entry = (FriendlistEntry) o;
        return Objects.equals(id, entry.id)
                && Objects.equals(friendlistId, entry.friendlistId)
                && Objects.equals(user, entry.user)
                && Objects.equals(createdOn, entry.createdOn);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, friendlistId, user, createdOn);
    }
}
