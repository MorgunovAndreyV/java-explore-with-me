package ru.practicum.friendshiprequest;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.stereotype.Repository;
import ru.practicum.friendshiprequest.model.FriendshipRequest;

@Repository
public interface FriendshipRequestRepository extends JpaRepository<FriendshipRequest, Long>,
        QuerydslPredicateExecutor<FriendshipRequest> {
}
