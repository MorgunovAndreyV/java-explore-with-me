package ru.practicum.comparators;


import ru.practicum.user.model.User;

import java.util.Comparator;

public class UserComparators {

    public static final Comparator<User> compareUsersById = (user1, user2) -> {
        if (user1.getId() != null && user2.getId() != null) {
            return user1.getId().compareTo(user2.getId());

        } else if (user1.getId() == null && user2.getId() != null) {
            return -1;

        } else if (user1.getId() != null) {
            return 1;

        } else {
            return 0;

        }

    };

}
