package ru.practicum.endpointhit;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exception.ViewStatControllerBadRequestException;

import java.time.format.DateTimeFormatter;

@RestController
@RequiredArgsConstructor
@Slf4j
public class EndpointHitController {
    private final EndpointHitClient endpointHitClient;

    @PostMapping(path = "/hit")
    public ResponseEntity<Object> addNew(@RequestBody EndpointHitDto hitDto) {
        validateEndpointHitData(hitDto);

        return endpointHitClient.addNew(hitDto);
    }

    @GetMapping(path = "/stats")
    public ResponseEntity<Object> getStats(@RequestParam(name = "start") String start,
                                           @RequestParam(name = "end") String end,
                                           @RequestParam(name = "uris", required = false) String[] uris,
                                           @RequestParam(name = "unique", required = false) Boolean unique) {
        if (!isValidDateFormat(start) || !isValidDateFormat(end)) {
            throw new ViewStatControllerBadRequestException("Формат даты в параметре запроса некорректен");
        }

        return endpointHitClient.getStats(start, end, uris, unique);
    }

    boolean isValidDateFormat(String dateString) {
        String format = "YYYY-MM-DD hh:mm:ss";

        try {
            //format example 2020-05-05 00:00:00
            DateTimeFormatter.ofPattern(format);
            return true;

        } catch (IllegalArgumentException e) {
            return false;
        }

    }

    void validateEndpointHitData(EndpointHitDto hitDto) {
        if (!isValidDateFormat(hitDto.getHitTimestamp().toString())) {
            throw new ViewStatControllerBadRequestException("Формат даты в теле запроса некорректен");
        }

    }

}
