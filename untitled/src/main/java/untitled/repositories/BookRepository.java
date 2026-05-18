package untitled.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable; // ВАЖЛИВИЙ ІМПОРТ
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import untitled.entities.BookEntity;

@Repository
public interface BookRepository extends JpaRepository<BookEntity, Long> {

    @Query("SELECT b FROM BookEntity b WHERE " +
            "(:categoryId IS NULL OR b.category.categoryId = :categoryId) AND " +
            "(:authorId IS NULL OR b.author.authorId = :authorId) AND " +
            "(CAST(:title AS string) IS NULL OR LOWER(b.title) LIKE LOWER(CONCAT('%', CAST(:title AS string), '%')))")
        // Змінили List на Page і додали Pageable в кінець
    Page<BookEntity> searchBooks(@Param("categoryId") Long categoryId,
                                 @Param("authorId") Long authorId,
                                 @Param("title") String title,
                                 Pageable pageable);
}