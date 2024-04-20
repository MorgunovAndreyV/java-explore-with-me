package ru.practicum.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    @Query(value = "select * " +
            "from events e " +
            "join requests r on r.event_id = e.id " +
            "join subscriptions s on r.requester_id =s.target_user_id " +
            "where s.user_id = :id and r.state = :requestStatus and e.event_date > :eventDateAfter",
            nativeQuery = true)
    Page<Event> findBySubscriberPaginated(@Param("id") Long userId,
                                          @Param("requestStatus") String requestStatus,
                                          @Param("eventDateAfter") LocalDateTime eventDateAfter,
                                          PageRequest pageRequest);

    @Query(value = "select * " +
            "from events e " +
            "join requests r on r.event_id = e.id " +
            "join subscriptions s on r.requester_id =s.target_user_id " +
            "where s.user_id = :id and r.state = :requestStatus and e.event_date > :eventDateAfter",
            nativeQuery = true)
    List<Event> findBySubscriber(@Param("id") Long userId,
                                 @Param("requestStatus") String requestStatus,
                                 @Param("eventDateAfter") LocalDateTime eventDateAfter);

    @Query(value = "select * " +
            "from events e " +
            "join requests r on r.event_id = e.id " +
            "join subscriptions s on r.requester_id =s.target_user_id " +
            "where s.user_id = :id and s.target_user_id =:targetUserId and r.state = :requestStatus " +
            "and e.event_date > :eventDateAfter",
            nativeQuery = true)
    List<Event> findBySubscriber(@Param("id") Long userId,
                                 @Param("targetUserId") Long targetUserId,
                                 @Param("requestStatus") String requestStatus,
                                 @Param("eventDateAfter") LocalDateTime eventDateAfter);

    @Query(value = "select * " +
            "from events e " +
            "join requests r on r.event_id = e.id " +
            "join subscriptions s on r.requester_id =s.target_user_id " +
            "where s.user_id = :id and s.target_user_id = :targetUserId and r.state = :requestStatus " +
            " and e.event_date > :eventDateAfter",
            nativeQuery = true)
    Page<Event> findBySubscriberPaginated(@Param("id") Long userId,
                                          @Param("targetUserId") Long targetUserId,
                                          @Param("requestStatus") String requestStatus,
                                          @Param("eventDateAfter") LocalDateTime eventDateAfter,
                                          PageRequest pageRequest);

}
