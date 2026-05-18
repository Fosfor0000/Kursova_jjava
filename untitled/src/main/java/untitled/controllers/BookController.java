package untitled.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import untitled.dto.BookRequestDto;
import untitled.dto.BookResponseDto;
import untitled.services.BookService;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "2. Каталог книг", description = "Ендпоінти для управління книгами (пошук, створення, видалення)")
public class BookController {

    private final BookService bookService;

    @GetMapping
    @Operation(summary = "Отримати список книг (Пошук + Пагінація)", description = "Повертає книги сторінками. Доступне комбінування фільтрів.")
    public ResponseEntity<Page<BookResponseDto>> getAllBooks(
            @Parameter(description = "ID жанру/категорії") @RequestParam(name = "categoryId", required = false) Long categoryId,
            @Parameter(description = "ID автора") @RequestParam(name = "authorId", required = false) Long authorId,
            @Parameter(description = "Назва або частина назви книги") @RequestParam(name = "title", required = false) String title,
            @Parameter(description = "Номер сторінки (починається з 0)") @RequestParam(name = "page", defaultValue = "0") int page,
            @Parameter(description = "Кількість елементів на сторінці") @RequestParam(name = "size", defaultValue = "10") int size) {
        return ResponseEntity.ok(bookService.getAllBooks(categoryId, authorId, title, page, size));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Знайти книгу за ID")
    public ResponseEntity<BookResponseDto> getBookById(
            @Parameter(description = "Унікальний ідентифікатор книги", required = true) @PathVariable(name = "id") Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Додати нову книгу", description = "Тільки для користувачів з роллю ADMIN")
    public ResponseEntity<BookResponseDto> createBook(@RequestBody BookRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookService.createBook(request));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @SecurityRequirement(name = "bearerAuth")
    @Operation(summary = "Видалити книгу", description = "Тільки для користувачів з роллю ADMIN")
    public ResponseEntity<Void> deleteBook(
            @Parameter(description = "ID книги для видалення", required = true) @PathVariable(name = "id") Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}