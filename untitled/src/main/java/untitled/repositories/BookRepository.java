package untitled.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import untitled.entities.BookEntity;

import java.util.List;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {
    // Пошук книг за категорією
    List<BookEntity> findByCategory_CategoryId(Long categoryId);

    // Пошук за автором
    List<BookEntity> findByAuthor_AuthorId(Long authorId);

    // Пошук за назвою
    List<BookEntity> findByTitleContainingIgnoreCase(String title);
}