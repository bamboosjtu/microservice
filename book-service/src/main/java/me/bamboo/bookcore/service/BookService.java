package me.bamboo.bookcore.service;

import java.time.Instant;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bamboo.bookcore.controller.BookDTO;
import me.bamboo.bookcore.message.KafkaDispatcher;
import me.bamboo.bookcore.model.Book;
import me.bamboo.bookcore.repository.BookRepository;

import me.bamboo.common.book.BookCreatedEvent;
import me.bamboo.common.book.BookDomainEvent;
import me.bamboo.common.book.BookEvent;
import me.bamboo.common.book.Booktype;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(transactionManager = "transactionManager")
public class BookService {
	private final BookRepository repository;
	private final KafkaDispatcher dispatcher;

	public Long save(BookDTO dto) {
		Book saved = this.repository.save(Book.builder().title(dto.title()).author(dto.author()).price(dto.price())
				.booktype(Booktype.valueOf(dto.type())).build());
		log.debug("Book saving process has been finished. {}", saved);
		var event = BookDomainEvent.<BookCreatedEvent>builder()
				.id(UUID.randomUUID())
                .type(BookEvent.EventType.BOOK_CREATED.getEventName())
                .created(Instant.now())
                .source(BookDomainEvent.SOURCE)
                .payload(new BookCreatedEvent(saved.getId().toString(), saved.getTitle(), saved.getAuthor(),saved.getPrice(), saved.getBooktype()))
                .build();
		dispatcher.send((BookDomainEvent) event);
		return saved.getId();
	}

}
