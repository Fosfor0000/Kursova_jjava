package untitled.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import untitled.dto.CategoryResponseDto;
import untitled.repositories.CategoryRepository;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "4.Довідник категорій", description = "Перегляд жанрів та категорій книг (Повністю на DTO)")

public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    @Operation(summary = "Отримати список усіх категорій", description = "Повертає перелік категорій у чистому форматі DTO, ізольованому від бази даних.")
    public ResponseEntity<List<CategoryResponseDto>> getAll() {
        List<CategoryResponseDto> categories = categoryRepository.findAll().stream()
                .map(category -> CategoryResponseDto.builder()
                        .categoryId(category.getCategoryId())
                        .name(category.getName())
                        .description(category.getDescription())
                        .build())
                .toList();

        return ResponseEntity.ok(categories);
    }
}