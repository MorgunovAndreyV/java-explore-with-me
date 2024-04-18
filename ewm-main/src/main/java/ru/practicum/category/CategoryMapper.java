package ru.practicum.category;

import lombok.experimental.UtilityClass;
import ru.practicum.category.model.Category;

@UtilityClass
public class CategoryMapper {

    public static Category toEntity(CategoryDto categoryDto) {
        return Category.builder()
                .name(categoryDto.getName())
                .build();
    }

    public static CategoryDto toDto(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }

    public static void fillFromDto(CategoryDto categoryDto, Category category) {
        if (categoryDto.getName() != null) {
            category.setName(categoryDto.getName());
        }

    }

}