package untitled.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import untitled.entities.AuthorEntity;

import java.util.List;

@Repository
public interface AuthorRepository extends JpaRepository<AuthorEntity, Long> {
    // Можна додати пошук за ім'ям
    List<AuthorEntity> findByNameContainingIgnoreCase(String name);
}