package ru.practicum.viewstat;

import lombok.experimental.UtilityClass;
import ru.practicum.endpointhit.EndpointHitDto;
import ru.practicum.endpointhit.model.EndpointHit;
import ru.practicum.viewstat.model.ViewStat;

@UtilityClass
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
