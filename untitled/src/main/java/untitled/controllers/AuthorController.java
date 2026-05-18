package untitled.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import untitled.dto.AuthorResponseDto;
import untitled.repositories.AuthorRepository;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
@Tag(name = "5. Довідник авторів", description = "API для читання інформації про авторів книг")
public class AuthorController {

    private final AuthorRepository authorRepository;

    @GetMapping
    @Operation(summary = "Отримати список усіх авторів", description = "Повертає перелік авторів у форматі DTO для безпечної передачі даних на клієнт.")
    public ResponseEntity<List<AuthorResponseDto>> getAll() {
        List<AuthorResponseDto> authors = authorRepository.findAll().stream()
                .map(author -> AuthorResponseDto.builder()
                        .authorId(author.getAuthorId())
                        .name(author.getName())
                        .bio(author.getBio())
                        .birthYear(author.getBirthYear())
                        .build())
                .toList();

        return ResponseEntity.ok(authors);
    }
}