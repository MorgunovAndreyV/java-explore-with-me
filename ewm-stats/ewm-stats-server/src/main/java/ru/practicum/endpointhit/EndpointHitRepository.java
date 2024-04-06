package ru.practicum.endpointhit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query(value = "SELECT * " +
            "FROM ENDPOINTHITS AS ephts " +
            "WHERE ephts.hittimestamp BETWEEN to_timestamp(:start, 'YYYY-MM-DD HH24:mi:ss') " +
            "AND to_timestamp(:end, 'YYYY-MM-DD HH24:mi:ss') ",
            nativeQuery = true)
    List<EndpointHit> findAllBetweenDates(@Param("start") String start, @Param("end") String end);

    @Query(value = "SELECT * " +
            "FROM ENDPOINTHITS AS ephts " +
            "WHERE ephts.hittimestamp BETWEEN to_timestamp(:start, 'YYYY-MM-DD HH24:mi:ss') " +
            "AND to_timestamp(:end, 'YYYY-MM-DD HH24:mi:ss') " +
            "AND ephts.uri IN (:uris)",
            nativeQuery = true)
    List<EndpointHit> findAllBetweenDatesAndUri(@Param("start") String start,
                                                @Param("end") String end,
                                                @Param("uris") List<String> uris);

}
