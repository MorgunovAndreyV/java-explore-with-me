package ru.practicum.comparators;

import ru.practicum.viewstat.ViewStatDto;

import java.util.Comparator;

public class ViewStatComparators {
    public static final Comparator<ViewStatDto> compareViewStatDtoByHitsDesc = (viewStatDto1, viewStatDto2) -> {
        if (viewStatDto1.getHits() != null && viewStatDto2.getHits() != null) {
            if (viewStatDto1.getHits() > viewStatDto2.getHits()) {

                return -1;
            } else if (viewStatDto1.getHits() < viewStatDto2.getHits()) {

                return 1;
            } else {

                return 1;
            }

        } else if (viewStatDto1.getHits() != null) {

            return -1;
        } else if (viewStatDto2.getHits() != null) {

            return 1;
        } else {

            return 1;
        }

    };
}
