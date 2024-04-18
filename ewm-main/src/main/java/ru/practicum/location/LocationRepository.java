package ru.practicum.location;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.location.model.Location;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long>, QuerydslPredicateExecutor<Location> {
}
