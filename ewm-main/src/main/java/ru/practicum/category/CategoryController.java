package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import ru.practicum.EwmMainService;
import ru.practicum.event.EventService;
import ru.practicum.event.model.Event;
import ru.practicum.stat.client.EndpointHitClient;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;


@RequiredArgsConstructor
@RestController
@RequestMapping
@Slf4j
public class CategoryController {
    private final CategoryService categoryService;
    private final EventService eventService;
    private final EndpointHitClient endpointHitClient;


    @PostMapping("/admin/categories")
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto addNew(@RequestBody CategoryDto categoryDto) {
        return CategoryMapper.toDto(categoryService.addNew(CategoryMapper.toEntity(categoryDto)));
    }

    @PatchMapping("/admin/categories/{catId}")
    public CategoryDto change(@PathVariable Long catId, @RequestBody CategoryDto categoryDto) {
        return CategoryMapper.toDto(categoryService.changeCategoryData(catId, CategoryMapper.toEntity(categoryDto)));
    }

    @DeleteMapping("/admin/categories/{catId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long catId) {
        List<Event> eventList = eventService.getEventsByCategory(catId);

        if (!eventList.isEmpty()) {
            throw new DataIntegrityViolationException("Невозможно удалить категорию - имеются связанные события");
        }

        categoryService.delete(catId);
    }

    @GetMapping("/categories")
    public List<CategoryDto> getPaginated(@Nullable @RequestParam(defaultValue = "0", required = false) Integer from,
                                          @Nullable @RequestParam(defaultValue = "10", required = false) Integer size,
                                          HttpServletRequest request) {
        endpointHitClient.registerHit(EwmMainService.APPTITLE, request);
        return categoryService.getCategoriesPaginated(from, size)
                .stream()
                .map(CategoryMapper::toDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/categories/{catId}")
    public CategoryDto getById(@PathVariable Long catId,
                               HttpServletRequest request) {
        endpointHitClient.registerHit(EwmMainService.APPTITLE, request);
        return CategoryMapper.toDto(categoryService.getCategoryById(catId));
    }

}
