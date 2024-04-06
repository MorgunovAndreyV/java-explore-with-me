package ru.practicum.viewstat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.exception.ViewStatControllerBadRequestException;

import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping(path = "/stats")
@RequiredArgsConstructor
@Slf4j
public class ViewStatController {
    private final ViewStatClient viewStatClient;



}
