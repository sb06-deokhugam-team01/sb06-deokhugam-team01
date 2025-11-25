package com.sprint.sb06deokhugamteam01.repository;

import com.sprint.sb06deokhugamteam01.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface BookRepository extends JpaRepository<Book, String> {

    boolean existsById(String id);

    boolean existsByIsbn(String isbn);

    Optional<Book> findById(UUID id);

    Optional<Book> findByIsbn(String isbn);

    void deleteById(UUID id);

}
