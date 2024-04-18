package ru.practicum.location;

import lombok.experimental.UtilityClass;
import ru.practicum.location.model.Location;

@UtilityClass
public class LocationMapper {

    public static LocationDto toDto(Location location) {
        return LocationDto.builder()
                .lat(location.getLat())
                .lon(location.getLon())
                .build();
    }

    public static Location toEntity(LocationDto locationDto) {
        return Location.builder()
                .lat(locationDto.getLat())
                .lon(locationDto.getLon())
                .build();
    }


}