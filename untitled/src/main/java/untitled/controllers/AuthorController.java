package untitled.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import untitled.entities.AuthorEntity;
import untitled.repositories.AuthorRepository;

import java.util.List;

@RestController
@RequestMapping("/api/authors")
@RequiredArgsConstructor
class AuthorController {
    private final AuthorRepository authorRepository;

    @GetMapping
    public List<AuthorEntity> getAll() { return authorRepository.findAll(); }
}

