package ru.practicum.event;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.practicum.event.model.Event;

import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long>, QuerydslPredicateExecutor<Event> {
    @Query(value = "SELECT * " +
            "FROM EVENTS AS ev " +
            "WHERE ev.initiator_id =:id",
            nativeQuery = true)
    List<Event> findByUserId(@Param("id") Long userId);

    @Query(value = "SELECT * " +
            "FROM EVENTS AS ev " +
            "WHERE ev.initiator_id =:id",
            nativeQuery = true)
    Page<Event> findByUserId(@Param("id") Long userId, PageRequest pageRequest);


    @Query(value = "SELECT * " +
            "FROM EVENTS AS ev " +
            "WHERE ev.initiator_id =:userId AND ev.id=:eventId",
            nativeQuery = true)
    Event findByUserIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

}
