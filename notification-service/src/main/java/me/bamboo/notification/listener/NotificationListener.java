package me.bamboo.notification.listener;

import java.io.IOException;
import java.util.Optional;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import me.bamboo.common.notification.NotificationDomainEvent;
import me.bamboo.notification.service.Email163Service;



@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {
	private final ObjectMapper om;
	private final Email163Service service;

	@SuppressWarnings("rawtypes")
	@KafkaListener(topics = "${app.kafka.notification.topic}", groupId = "${spring.application.name}")
	public void process(@Payload String payload) {
		Optional.ofNullable(payload).ifPresentOrElse(event -> {
			deserialize(event).ifPresent(emailTriggeredEvent -> {
				System.out.println(emailTriggeredEvent);
				service.sendEmailWithHtmlTemplate(emailTriggeredEvent);
			});
		}, () -> {
			throw new IllegalArgumentException("Payload cannot be null");
		});
	}

	private Optional<NotificationDomainEvent> deserialize(String et) {
		try {
			return Optional.of(om.readValue(et, NotificationDomainEvent.class));
		} catch (IOException e) {
			log.warn("EmailTriggeredEvent解析错误: {}", e.getMessage());
		}
		return Optional.empty();
	}
}
