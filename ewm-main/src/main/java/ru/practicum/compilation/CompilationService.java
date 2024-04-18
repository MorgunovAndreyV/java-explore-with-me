package ru.practicum.compilation;

import com.querydsl.core.BooleanBuilder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.compilation.model.Compilation;
import ru.practicum.compilation.model.QCompilation;
import ru.practicum.event.EventService;
import ru.practicum.exception.CategoryValidationException;
import ru.practicum.exception.CompilationValidationException;
import ru.practicum.exception.RecordNotFoundException;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Slf4j
@Service
@RequiredArgsConstructor
public class CompilationService {
    private final CompilationRepository compilationRepository;
    private final EventService eventService;

    public Compilation addNew(NewCompilationDto compilationDto) {
        validateCompilationCreateInformation(compilationDto);

        Compilation newCompilation = CompilationMapper.toEntityNew(compilationDto);
        newCompilation.setEvents(compilationDto.getEvents() != null ? compilationDto.getEvents().stream()
                .map(eventService::getEventById)
                .peek(event -> event.addCompilation(newCompilation))
                .collect(Collectors.toSet()) : new HashSet<>());

        compilationRepository.save(newCompilation);
        log.info("Новая подборка добавлена успешно. id:" + newCompilation.getId());

        return newCompilation;
    }

    public Compilation getCompilationById(Long id) {
        Compilation compilationFromBase = compilationRepository.findById(id).orElse(null);

        if (compilationFromBase == null) {
            throw new RecordNotFoundException("Подборка с id " + id + " не найдена.");
        }
        return compilationFromBase;
    }

    public Compilation changeCompilationData(Long id, UpdateCompilationRequest compilationDto) throws RecordNotFoundException {
        Compilation categoryFromBase = getCompilationById(id);

        validateCompilationUpdateInformation(compilationDto);

        CompilationMapper.fillFromDto(compilationDto, categoryFromBase);

        categoryFromBase = compilationRepository.save(categoryFromBase);

        log.info("Запись подборки изменена успешно. id:" + categoryFromBase.getId());

        return categoryFromBase;
    }

    public List<Compilation> getCompilationsPaginated(Boolean pinned, Integer from, Integer size) {
        validatePagination(from, size);

        BooleanBuilder bd = new BooleanBuilder();
        PageRequest pageRequest;

        if (pinned != null) {
            bd.and(QCompilation.compilation.pinned.eq(pinned));
        } else {
            bd.and(QCompilation.compilation.pinned.eq(false));
        }

        if (size != null && from != null) {
            //Sort sortByStartDate = Sort.by(Sort.Direction.DESC, "start");
            pageRequest = PageRequest.of(from > 0 ? from / size : 0, size/*, sortByStartDate*/);
            return compilationRepository.findAll(bd, pageRequest).getContent();

        } else {
            return StreamSupport.stream(compilationRepository.findAll(bd).spliterator(), false)
                    .collect(Collectors.toList());
        }

    }


    public void delete(Long id) throws RecordNotFoundException {
        getCompilationById(id);
        compilationRepository.deleteById(id);

    }

    void validateCompilationCreateInformation(NewCompilationDto compilation) {
        if (compilation.getPinned() == null) {
            compilation.setPinned(false);
        }

        if (compilation.getTitle() == null || compilation.getTitle().isEmpty() || compilation.getTitle().trim().isEmpty()) {
            throw new CompilationValidationException("Имя подборки не может быть пустым");
        } else if (compilation.getTitle().length() > 50) {
            throw new CompilationValidationException("Неверная длина имени подборки");
        }

    }

    void validateCompilationUpdateInformation(UpdateCompilationRequest compilation) {
        if (compilation.getTitle() != null) {
            if (compilation.getTitle().isEmpty() || compilation.getTitle().trim().isEmpty()) {
                throw new CompilationValidationException("Имя подборки не может быть пустым");
            } else if (compilation.getTitle().length() > 50) {
                throw new CompilationValidationException("Неверная длина имени подборки");
            }

        }

    }

    private void validatePagination(Integer numberFrom, Integer pageSize) {
        if ((pageSize != null && numberFrom != null) && (pageSize < 1 || numberFrom < 0)) {
            throw new CategoryValidationException("Некорректные параметры запроса с постраничным выводом");

        }

    }

}
