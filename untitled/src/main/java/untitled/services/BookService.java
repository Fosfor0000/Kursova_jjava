package untitled.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import untitled.dto.BookRequestDto;
import untitled.dto.BookResponseDto;
import untitled.entities.AuthorEntity;
import untitled.entities.BookEntity;
import untitled.entities.CategoryEntity;
import untitled.exceptions.BusinessException;
import untitled.repositories.AuthorRepository;
import untitled.repositories.BookRepository;
import untitled.repositories.CategoryRepository;

@Service
@RequiredArgsConstructor
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    // Отримання списку книг із підтримкою пагінації та динамічного фільтрування
    public Page<BookResponseDto> getAllBooks(Long categoryId, Long authorId, String title, int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        return bookRepository.searchBooks(categoryId, authorId, title, pageable)
                .map(this::mapToResponseDto);
    }

    // Пошук конкретної книги за ідентифікатором
    public BookResponseDto getBookById(Long id) {
        BookEntity book = bookRepository.findById(id)
                .orElseThrow(() -> new BusinessException("Книгу з ID " + id + " не знайдено"));

        return mapToResponseDto(book);
    }

    // Додавання нової книги в каталог із перевіркою існування зв'язаних сутностей
    public BookResponseDto createBook(BookRequestDto request) {
        AuthorEntity author = authorRepository.findById(request.authorId())
                .orElseThrow(() -> new BusinessException("Автора з ID " + request.authorId() + " не знайдено"));

        CategoryEntity category = categoryRepository.findById(request.categoryId())
                .orElseThrow(() -> new BusinessException("Категорію з ID " + request.categoryId() + " не знайдено"));

        BookEntity book = new BookEntity();
        book.setTitle(request.title());
        book.setAuthor(author);
        book.setCategory(category);
        book.setIsbn(request.isbn());
        book.setPrice(request.price());
        book.setStock(request.stock() != null ? request.stock() : 0);
        book.setDescription(request.description());
        book.setPublishedYear(request.publishedYear());
        book.setCoverImage(request.coverImage());

        BookEntity savedBook = bookRepository.save(book);
        return mapToResponseDto(savedBook);
    }

    // Видалення книги з бази даних
    public void deleteBook(Long id) {
        if (!bookRepository.existsById(id)) {
            throw new BusinessException("Книгу з ID " + id + " не знайдено");
        }
        bookRepository.deleteById(id);
    }

    // Конвертація сутності в DTO з безпечним опрацюванням відсутніх зв'язків (null-safety)
    private BookResponseDto mapToResponseDto(BookEntity book) {
        return BookResponseDto.builder()
                .bookId(book.getBookId())
                .title(book.getTitle())
                .authorName(book.getAuthor() != null ? book.getAuthor().getName() : "Невідомий автор")
                .categoryName(book.getCategory() != null ? book.getCategory().getName() : "Без категорії")
                .isbn(book.getIsbn())
                .price(book.getPrice())
                .stock(book.getStock())
                .description(book.getDescription())
                .publishedYear(book.getPublishedYear())
                .coverImage(book.getCoverImage())
                .build();
    }
}