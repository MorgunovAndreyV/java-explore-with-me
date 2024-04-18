package ru.practicum.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.category.model.Category;
import ru.practicum.exception.CategoryValidationException;
import ru.practicum.exception.RecordNotFoundException;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    public Category addNew(Category category) {
        validateUserInformation(category);

        Category newCategory = categoryRepository.save(category);

        log.info("Новая категория добавлена успешно. id:" + category.getId());

        return newCategory;
    }

    public Category changeCategoryData(Long id, Category category) throws RecordNotFoundException {
        validateUserInformation(category);
        Category categoryFromBase = getCategoryById(id);

        CategoryMapper.fillFromDto(CategoryMapper.toDto(category), categoryFromBase);

        log.info("Запись категории изменена успешно. id:" + category.getId());

        return categoryRepository.save(categoryFromBase);
    }

    public void delete(Long id) throws RecordNotFoundException {
        getCategoryById(id);

        categoryRepository.deleteById(id);

    }

    public Category getCategoryById(Long id) {
        Category categoryFromBase = categoryRepository.findById(id).orElse(null);

        if (categoryFromBase == null) {
            throw new RecordNotFoundException("Категория с id " + id + " не найдена.");
        }
        return categoryFromBase;
    }

    public List<Category> getCategoriesPaginated(Integer from, Integer size) {
        validatePagination(from, size);
        PageRequest pageRequest;

        if (size != null && from != null) {
            pageRequest = PageRequest.of(from > 0 ? from / size : 0, size);
            return categoryRepository.findAll(pageRequest).getContent();

        } else {
            return categoryRepository.findAll();
        }

    }

    void validateUserInformation(Category category) {
        if (category.getName() == null || category.getName().isEmpty() || category.getName().trim().isEmpty()) {
            throw new CategoryValidationException("Имя категории не может быть пустым");
        } else if (category.getName().length() > 50) {
            throw new CategoryValidationException("Неверная длина имени категории");
        }

    }

    private void validatePagination(Integer numberFrom, Integer pageSize) {
        if ((pageSize != null && numberFrom != null) && (pageSize < 1 || numberFrom < 0)) {
            throw new CategoryValidationException("Некорректные параметры запроса с постраничным выводом");

        }

    }
}