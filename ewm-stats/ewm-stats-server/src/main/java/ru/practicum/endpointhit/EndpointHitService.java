package ru.practicum.endpointhit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.endpointhit.model.EndpointHit;
import ru.practicum.exception.EndpointHitControllerBadRequestException;
import ru.practicum.viewstat.model.ViewStat;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EndpointHitService {
    private final EndpointHitRepository endpointHitRepository;
    public static final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public List<EndpointHit> getBetweenDates(String start, String end) {

        return endpointHitRepository.findAllBetweenDates(start, end);
    }

    public EndpointHit addNew(EndpointHitDto endpointHitDto) {

        EndpointHit newEndpointHit = endpointHitRepository.save(EndpointHitMapper.toEntity(endpointHitDto));

        log.info("Посещение app:" + endpointHitDto.getApp() + ", endpoint:" + endpointHitDto.getUri() + " сохранено");

        return newEndpointHit;
    }

    public List<ViewStat> getViewStats(String start, String end, String[] uris, Boolean unique) {
        validateEndpointHitDates(start, end);

        List<ViewStat> outList = new ArrayList<>();
        List<EndpointHit> resultingHits;

        if (uris == null) {
            resultingHits = endpointHitRepository.findAllBetweenDates(start, end);

        } else {
            resultingHits = endpointHitRepository.findAllBetweenDatesAndUri(start, end, Arrays.asList(uris));

        }

        if (unique == null) {
            unique = false;
        }

        int endpointHitCount;

        for (String app : resultingHits.stream().map(EndpointHit::getApp).collect(Collectors.toSet())) {
            for (String endpoint : resultingHits.stream()
                    .filter(endpointHit -> app.equals(endpointHit.getApp()))
                    .map(EndpointHit::getUri)
                    .collect(Collectors.toSet())) {
                endpointHitCount = 0;

                for (String ip : resultingHits.stream()
                        .filter(endpointHit -> app.equals(endpointHit.getApp())
                                && endpoint.equals(endpointHit.getUri()))
                        .map(EndpointHit::getIp)
                        .collect(Collectors.toSet())) {
                    if (!unique) {
                        endpointHitCount = endpointHitCount + (int) resultingHits.stream()
                                .filter(endpointHit -> app.equals(endpointHit.getApp())
                                        && endpoint.equals(endpointHit.getUri())
                                        && ip.equals(endpointHit.getIp())).count();

                    } else {
                        endpointHitCount = endpointHitCount + 1;

                    }

                }

                outList.add(ViewStat.builder().app(app).uri(endpoint).hits(endpointHitCount).build());

            }

        }

        return outList;
    }

    void validateEndpointHitDates(String start, String end) {
        if (getDateFromString(start).isAfter(getDateFromString(end))) {
            throw new EndpointHitControllerBadRequestException(("Дата начала диапазона не может быть позже " +
                    "даты его конца"));
        }

    }

    LocalDateTime getDateFromString(String dateString) {
        try {
            return LocalDateTime.parse(dateString, dateTimeFormat);

        } catch (IllegalArgumentException e) {

            throw new EndpointHitControllerBadRequestException(("Формат даты в теле запроса некорректен"));
        }

    }
}
