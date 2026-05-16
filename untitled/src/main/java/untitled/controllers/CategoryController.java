package untitled.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import untitled.entities.CategoryEntity;
import untitled.repositories.CategoryRepository;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
class CategoryController {
    private final CategoryRepository categoryRepository;

    @GetMapping
    public List<CategoryEntity> getAll() {
        return categoryRepository.findAll();
    }
}
