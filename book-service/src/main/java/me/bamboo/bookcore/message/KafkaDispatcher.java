package me.bamboo.bookcore.message;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bamboo.common.book.BookCreatedEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaDispatcher {
	private final KafkaTemplate<String, String> template;
	private final ObjectMapper om;
	
	@Value("${app.kafka.book.topic}")
	private String bookTopic;
	
	public void send(BookCreatedEvent bookCreatedEvent) {
		String event = serialize(bookCreatedEvent);
		template.send(bookTopic, event);
		log.debug("Sending message {} to {}", event, bookTopic);
	}
	
	private String serialize(Object event) {
		try {
			return this.om.writeValueAsString(event);
		} catch (JsonProcessingException e) {
			log.warn("{} Event Send exsits error.", e.getMessage());
		}
		return "Event Error";
	}
}
