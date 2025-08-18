package me.bamboo.percolator.listener;

import java.io.IOException;
import java.util.Optional;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bamboo.common.book.BookDomainEvent;
import me.bamboo.common.search_preference.SearchPreferenceDomainEvent;
import me.bamboo.percolator.message.KafkaDispatcher;
import me.bamboo.percolator.service.PercolatorService;

@Slf4j
@Component
@RequiredArgsConstructor
public class BookListener {
	private final ObjectMapper om;
	private final PercolatorService service;
	private final KafkaDispatcher dispatcher;

	@SuppressWarnings("rawtypes")
	@KafkaListener(topics = "${app.kafka.book.topic}", groupId = "${spring.application.name}")
	public void process(@Payload String payload) {
		Optional.ofNullable(payload).ifPresentOrElse(event -> {
			deserialize(event).ifPresent(bookCreatedEvent -> {
				service.findMatches(bookCreatedEvent).forEach(spte -> dispatcher.send((SearchPreferenceDomainEvent) spte));
			});
		}, () -> {
			throw new IllegalArgumentException("Payload cannot be null");
		});
	}

	private Optional<BookDomainEvent> deserialize(String spc) {
		try {
			return Optional.of(om.readValue(spc, BookDomainEvent.class));
		} catch (IOException e) {
			log.warn("BookCreatedEvent Listen exsits error: {}", e.getMessage());
		}
		return Optional.empty();
	}
}
