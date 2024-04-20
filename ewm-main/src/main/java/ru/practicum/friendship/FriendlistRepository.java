package ru.practicum.friendship;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.friendship.model.Friendlist;

@Repository
public interface FriendlistRepository extends JpaRepository<Friendlist, Long>, QuerydslPredicateExecutor<Friendlist> {
}
