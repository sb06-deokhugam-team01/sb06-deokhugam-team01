package com.sprint.sb06deokhugamteam01.repository;

import com.sprint.sb06deokhugamteam01.domain.book.Book;
import com.sprint.sb06deokhugamteam01.repository.book.BookQRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends BookQRepository {

    boolean existsById(UUID id);

    boolean existsByIdAndIsActive(UUID id, boolean isActive);

    boolean existsByIsbn(String isbn);

    boolean existsByIsbnAndIsActive(String isbn, boolean isActive);

    Optional<Book> findById(UUID id);

    Optional<Book> findByIdAndIsActive(UUID id, boolean isActive);

    Optional<Book> findByIsbn(String isbn);

    Optional<Book> findByIsbnAndIsActive(String isbn, boolean isActive);

    void deleteById(UUID id);

    @Modifying
    @Query("DELETE FROM Book b WHERE b.isActive = false")
    void deleteAllByIsActiveFalse();

}
