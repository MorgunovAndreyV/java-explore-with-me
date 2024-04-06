package ru.practicum.viewstat;

import ru.practicum.endpointhit.EndpointHit;
import ru.practicum.endpointhit.EndpointHitDto;

public class ViewStatsMapper {

    public static ViewStatDto toDto(ViewStat viewStat) {
        return ViewStatDto.builder()
                .app(viewStat.getApp())
                .uri(viewStat.getUri())
                .hits(viewStat.getHits())
                .build();
    }

    public static EndpointHit toEntity(EndpointHitDto endpointHitDto) {
        return EndpointHit.builder()
                .app(endpointHitDto.getApp())
                .uri(endpointHitDto.getUri())
                .ip(endpointHitDto.getIp())
                .hitTimestamp(endpointHitDto.getHitTimestamp())
                .build();
    }

}
