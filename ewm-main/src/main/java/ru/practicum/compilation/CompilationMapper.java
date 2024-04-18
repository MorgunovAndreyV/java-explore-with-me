package ru.practicum.compilation;

import lombok.experimental.UtilityClass;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.event.EventMapper;
import ru.practicum.event.model.Event;

import java.util.HashSet;
import java.util.stream.Collectors;

@UtilityClass
public class CompilationMapper {

    public static CompilationDto toDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents().stream().map(EventMapper::toShortDto).collect(Collectors.toSet()))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    public static Compilation toEntityNew(NewCompilationDto compilationDto) {
        return Compilation.builder()
                .events(compilationDto.getEvents() != null ?
                        compilationDto.getEvents().stream()
                                .map(id -> Event.builder().id(id).build()).collect(Collectors.toSet()) :
                        new HashSet<>())
                .pinned(compilationDto.getPinned())
                .title(compilationDto.getTitle())
                .build();
    }

    public static void fillFromDto(UpdateCompilationRequest compilationDto, Compilation compilation) {
        if (compilationDto.getEvents() != null) {
            compilation.setEvents(compilationDto.getEvents().stream().map(id -> Event.builder().id(id).build()).collect(Collectors.toSet()));
        }

        if (compilationDto.getPinned() != null) {
            compilation.setPinned(compilationDto.getPinned());
        }

        if (compilationDto.getTitle() != null) {
            compilation.setTitle(compilationDto.getTitle());
        }
    }

}
