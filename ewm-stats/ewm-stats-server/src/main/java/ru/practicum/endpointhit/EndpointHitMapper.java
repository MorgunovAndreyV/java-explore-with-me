package ru.practicum.endpointhit;

import lombok.experimental.UtilityClass;

@UtilityClass
public class EndpointHitMapper {

    public static EndpointHitDto toDto(EndpointHit endpointHit) {
        return EndpointHitDto.builder()
                .app(endpointHit.getApp())
                .uri(endpointHit.getUri())
                .ip(endpointHit.getIp())
                .hitTimestamp(endpointHit.getHitTimestamp())
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
