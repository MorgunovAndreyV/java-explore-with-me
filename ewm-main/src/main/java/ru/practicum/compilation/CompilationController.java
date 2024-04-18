package ru.practicum.compilation;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EwmMainService;
import ru.practicum.stat.client.EndpointHitClient;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class CompilationController {
    private final CompilationService compilationService;
    private final EndpointHitClient endpointHitClient;

    @PostMapping("/admin/compilations")
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto addNew(@RequestBody NewCompilationDto compilationDto) {
        return CompilationMapper.toDto(compilationService.addNew(compilationDto));
    }

    @PatchMapping("/admin/compilations/{compId}")
    public CompilationDto change(@PathVariable Long compId, @RequestBody UpdateCompilationRequest compilationDto) {
        return CompilationMapper.toDto(compilationService.changeCompilationData(compId, compilationDto));
    }

    @DeleteMapping("/admin/compilations/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long compId) {
        compilationService.delete(compId);
    }

    @GetMapping("/compilations")
    public List<CompilationDto> getCompilationsPaginated(@Nullable @RequestParam(required = false) Boolean pinned,
                                                         @Nullable @RequestParam(defaultValue = "0", required = false) Integer from,
                                                         @Nullable @RequestParam(defaultValue = "10", required = false) Integer size,
                                                         HttpServletRequest request) {
        endpointHitClient.registerHit(EwmMainService.APPTITLE, request);
        return compilationService.getCompilationsPaginated(pinned, from, size)
                .stream()
                .map(CompilationMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/compilations/{compId}")
    public CompilationDto getById(@PathVariable Long compId,
                                  HttpServletRequest request) {
        endpointHitClient.registerHit(EwmMainService.APPTITLE, request);
        return CompilationMapper.toDto(compilationService.getCompilationById(compId));
    }

}
