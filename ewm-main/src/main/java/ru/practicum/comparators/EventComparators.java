package ru.practicum.comparators;


import ru.practicum.event.model.Event;

import java.util.Comparator;

public class EventComparators {

    public static final Comparator<Event> compareEventsByEventDate = (event1, event2) -> {
        if (event1.getEventDate() != null && event2.getEventDate() != null) {
            return event1.getEventDate().compareTo(event2.getEventDate());

        } else if (event1.getEventDate() == null && event2.getEventDate() != null) {
            return -1;

        } else if (event1.getEventDate() != null) {
            return 1;

        } else {
            return 0;

        }

    };

    public static final Comparator<Event> compareEventsByViews = (event1, event2) -> {
        if (event1.getViews() != null && event2.getViews() != null) {
            return event1.getViews().compareTo(event2.getViews());

        } else if (event1.getViews() == null && event2.getViews() != null) {
            return -1;

        } else if (event1.getViews() != null) {
            return 1;

        } else {
            return 0;

        }

    };

    public static final Comparator<Event> compareEventsById = (event1, event2) -> {
        if (event1.getId() != null && event2.getId() != null) {
            return event1.getId().compareTo(event2.getId());

        } else if (event1.getId() == null && event2.getId() != null) {
            return -1;

        } else if (event1.getId() != null) {
            return 1;

        } else {
            return 0;

        }

    };

}
