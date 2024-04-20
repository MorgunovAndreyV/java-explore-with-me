package ru.practicum.comparators;


import ru.practicum.subscription.model.Subscription;

import java.util.Comparator;

public class SubscriptionComparators {

    public static final Comparator<Subscription> compareEventsByUserid = (subscription1, subscription2) -> {
        if (subscription1.getUser().getId() != null && subscription2.getUser().getId() != null) {
            return subscription1.getUser().getId().compareTo(subscription2.getUser().getId());

        } else if (subscription1.getUser().getId() == null && subscription2.getUser().getId() != null) {
            return -1;

        } else if (subscription1.getUser().getId() != null) {
            return 1;

        } else {
            return 0;

        }

    };

}
