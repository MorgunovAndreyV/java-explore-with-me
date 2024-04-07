package ru.practicum.endpointhit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.viewstat.ViewStatDto;
import ru.practicum.viewstat.ViewStatsMapper;

import java.net.URLDecoder;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.comparators.ViewStatComparators.compareViewStatDtoByHitsDesc;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class EndpointHitController {
    private final EndpointHitService endpointHitService;

    @GetMapping(path = "/stats")
    public List<ViewStatDto> getStats(@RequestParam(name = "start") String start,
                                      @RequestParam(name = "end") String end,
                                      @RequestParam(name = "uris", required = false) String[] uris,
                                      @RequestParam(name = "unique", required = false) Boolean unique) {

        return endpointHitService.getViewStats(URLDecoder.decode(start), URLDecoder.decode(end), uris, unique).stream()
                .map(ViewStatsMapper::toDto).sorted(compareViewStatDtoByHitsDesc).collect(Collectors.toList());
    }

    @PostMapping(path = "/hit")
    @ResponseStatus(HttpStatus.CREATED)
    public EndpointHitDto addnew(@RequestBody EndpointHitDto endpointHitDto) {
        endpointHitDto.setHitTimestamp(LocalDateTime.now());

        return EndpointHitMapper.toDto(endpointHitService.addNew(endpointHitDto));
    }

}

