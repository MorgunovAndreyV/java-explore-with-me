package ru.practicum.subscription.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "subscriptions")
public class Subscription {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    @OneToOne
    @JoinColumn(name = "target_user_id")
    private User targetUser;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public User getTargetUser() {
        return targetUser;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }
}
