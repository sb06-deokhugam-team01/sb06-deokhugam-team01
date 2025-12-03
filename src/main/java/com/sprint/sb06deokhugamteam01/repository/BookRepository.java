package com.sprint.sb06deokhugamteam01.repository;

import com.sprint.sb06deokhugamteam01.domain.Book;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends BookQRepository {

    boolean existsById(UUID id);

    boolean existsByIsbn(String isbn);

    Optional<Book> findById(UUID id);

    Optional<Book> findByIsbn(String isbn);

    void deleteById(UUID id);

}
