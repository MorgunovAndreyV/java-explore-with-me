package ru.practicum.friendshiprequest.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.*;
import java.time.LocalDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "friendshiprequests")
public class FriendshipRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "source_user_id", nullable = false)
    private Long sourceUserId;
    @Column(name = "target_user_id", nullable = false)
    private Long targetUserId;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;


    public void confirmRequest() {
        if (Status.WAITING.equals(status)) {
            status = Status.CONFIRMED;
        } else {
            throw new DataIntegrityViolationException("Невозможно подтвердить заявку в состоянии " + status);
        }
    }

    public void declineRequest() {
        if (Status.WAITING.equals(status)) {
            status = Status.DECLINED;
        } else {
            throw new DataIntegrityViolationException("Невозможно отклонить заявку в состоянии " + status);
        }
    }

    public void cancelRequest() {
        if (Status.WAITING.equals(status)) {
            status = Status.CANCELLED;
        } else {
            throw new DataIntegrityViolationException("Невозможно отменить заявку в состоянии " + status);
        }
    }

    public Long getId() {
        return id;
    }

    public Long getSourceUserId() {
        return sourceUserId;
    }

    public Long getTargetUserId() {
        return targetUserId;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public Status getStatus() {
        return status;
    }
}
