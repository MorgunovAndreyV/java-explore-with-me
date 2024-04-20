package ru.practicum.event.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.category.model.Category;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.location.model.Location;
import ru.practicum.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "annotation", nullable = false)
    private String annotation;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;
    @ManyToOne
    @JoinColumn(name = "initiator_id")
    private User initiator;
    @ManyToOne
    @JoinColumn(name = "location_id")
    private Location location;
    @ManyToMany(mappedBy = "events")
    private Set<Compilation> compilations;
    @Column(name = "state", nullable = false)
    @Enumerated(EnumType.STRING)
    private State state;
    @Column(name = "eventDate", nullable = false)
    private LocalDateTime eventDate;
    @Column(name = "paid", nullable = false)
    private Boolean paid;
    @Column(name = "created_on", nullable = false)
    private LocalDateTime createdOn;
    @Column(name = "published_on", nullable = false)
    private LocalDateTime publishedOn;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "participant_limit", nullable = false)
    private Integer participantLimit;
    @Column(name = "request_moderation", nullable = false)
    private Boolean requestModeration;
    @Column(name = "confirmed_requests", nullable = false)
    private Integer confirmedRequests;
    @Transient
    private Integer views;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAnnotation() {
        return annotation;
    }

    public Category getCategory() {
        return category;
    }

    public User getInitiator() {
        return initiator;
    }

    public Location getLocation() {
        return location;
    }

    public Set<Compilation> getCompilations() {
        return compilations;
    }

    public State getState() {
        return state;
    }

    public LocalDateTime getEventDate() {
        return eventDate;
    }

    public Boolean getPaid() {
        return paid;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public LocalDateTime getPublishedOn() {
        return publishedOn;
    }

    public String getDescription() {
        return description;
    }

    public Integer getParticipantLimit() {
        return participantLimit;
    }

    public Boolean getRequestModeration() {
        return requestModeration;
    }

    public Integer getConfirmedRequests() {
        return confirmedRequests;
    }

    public Integer getViews() {
        return views;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public void setInitiator(User initiator) {
        this.initiator = initiator;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void setCompilations(Set<Compilation> compilations) {
        this.compilations = compilations;
    }

    public void setState(State state) {
        this.state = state;
    }

    public void setEventDate(LocalDateTime eventDate) {
        this.eventDate = eventDate;
    }

    public void setPaid(Boolean paid) {
        this.paid = paid;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public void setPublishedOn(LocalDateTime publishedOn) {
        this.publishedOn = publishedOn;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setParticipantLimit(Integer participantLimit) {
        this.participantLimit = participantLimit;
    }

    public void setRequestModeration(Boolean requestModeration) {
        this.requestModeration = requestModeration;
    }

    public void setConfirmedRequests(Integer confirmedRequests) {
        this.confirmedRequests = confirmedRequests;
    }

    public void setViews(Integer views) {
        this.views = views;
    }

    public void publishEvent() {
        if (State.PENDING.equals(this.state)) {
            state = State.PUBLISHED;
            publishedOn = LocalDateTime.now();

        } else {
            throw new DataIntegrityViolationException("Невозможно опубликовать событие в состоянии " + state);
        }

    }

    public void cancelEvent() {
        if (!State.PUBLISHED.equals(this.state)) {
            state = State.CANCELED;
        } else {
            throw new DataIntegrityViolationException("Невозможно отклонить событие в состоянии " + state);
        }

    }

    public void sendToReview() {
        if (State.CANCELED.equals(this.state)) {
            state = State.PENDING;
        } else {
            throw new DataIntegrityViolationException("Невозможно отправить на модерацию событие в состоянии " + state);
        }

    }

    public void cancelReview() {
        if (State.PENDING.equals(this.state)) {
            state = State.CANCELED;
        } else {
            throw new DataIntegrityViolationException("Невозможно отменить модерацию события в состоянии " + state);
        }

    }

    public void addCompilation(Compilation compilation) {
        compilations.add(compilation);
    }


}
