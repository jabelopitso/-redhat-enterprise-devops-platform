package com.redhatdevops.bookstore.repository;

import com.redhatdevops.bookstore.entity.Book;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookRepository extends JpaRepository<Book, Long> {
}