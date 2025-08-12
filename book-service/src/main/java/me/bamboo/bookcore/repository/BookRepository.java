package me.bamboo.bookcore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import me.bamboo.bookcore.model.Book;

public interface BookRepository extends JpaRepository<Book, Long> {

}
