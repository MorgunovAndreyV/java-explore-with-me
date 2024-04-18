package ru.practicum.location;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.location.model.Location;
import ru.practicum.location.model.QLocation;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {
    private final LocationRepository locationRepository;

    public Location addNew(Location location) {

        Location locationFromBase;
        BooleanBuilder bd = new BooleanBuilder();

        bd.and(QLocation.location.lat.eq(location.getLat()));
        bd.and(QLocation.location.lon.eq(location.getLon()));

        List<Location> locationsFromBase = StreamSupport.stream(locationRepository.findAll(bd).spliterator(), false)
                .collect(Collectors.toList());

        if (!locationsFromBase.isEmpty()) {
            locationFromBase = locationsFromBase.get(0);

            log.info("Локация с координатами LAT:" + location.getLat() + " LON:" + location.getLon() + " уже существует. " +
                    " ID: " + locationFromBase.getId());

            return locationFromBase;
        }

        locationFromBase = locationRepository.save(location);

        log.info("Новая локация добавлена успешно. id:" + locationFromBase.getId());

        return locationFromBase;
    }


}
