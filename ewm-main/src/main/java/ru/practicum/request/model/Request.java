package ru.practicum.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requests")
public class Request {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "event_id", nullable = false)
    private Long eventId;
    @Column(name = "requester_id", nullable = false)
    private Long requesterId;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdDate;
    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;

    public void confirmRequest() {
        if (State.PENDING.equals(state)) {
            state = State.CONFIRMED;

        } else {
            throw new DataIntegrityViolationException("Невозможно подтвердить заявку в состоянии " + state);
        }

    }

    public void rejectRequest() {
        if (!State.CONFIRMED.equals(state)) {
            state = State.REJECTED;
        } else {
            throw new DataIntegrityViolationException("Невозможно отклонить заявку в состоянии " + state);
        }

    }

}
