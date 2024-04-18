package ru.practicum.endpointhit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.endpointhit.model.EndpointHit;
import ru.practicum.viewstat.model.ViewStat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class EndpointHitService {
    private final EndpointHitRepository endpointHitRepository;

    public List<EndpointHit> getBetweenDates(String start, String end) {

        return endpointHitRepository.findAllBetweenDates(start, end);
    }

    public EndpointHit addNew(EndpointHitDto endpointHitDto) {

        EndpointHit newEndpointHit = endpointHitRepository.save(EndpointHitMapper.toEntity(endpointHitDto));

        log.info("Посещение app:" + endpointHitDto.getApp() + ", endpoint:" + endpointHitDto.getUri() + " сохранено");

        return newEndpointHit;
    }

    public List<ViewStat> getViewStats(String start, String end, String[] uris, Boolean unique) {
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
}
