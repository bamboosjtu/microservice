package me.bamboo.accountcore.listener;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import me.bamboo.accountcore.controller.SearchPreferenceDTO;
import me.bamboo.accountcore.message.KafkaDispatcher;
import me.bamboo.accountcore.service.AccountService;
import me.bamboo.accountcore.service.SearchPrefenceService;
import me.bamboo.common.book.Booktype;
import me.bamboo.common.notification.EmailTriggered;
import me.bamboo.common.notification.NotificationDomainEvent;
import me.bamboo.common.notification.NotificationEvent;
import me.bamboo.common.notification.NotificationType;
import me.bamboo.common.search_preference.SearchPreferenceDomainEvent;
import me.bamboo.common.search_preference.SearchPreferenceTriggeredEvent;


@Slf4j
@Component
@RequiredArgsConstructor
public class SearchPreferenceListener {
	private final ObjectMapper om;
	private final SearchPrefenceService searchPrefenceService;
	private final AccountService accountService;
	private final KafkaDispatcher dispatcher;

	@SuppressWarnings("rawtypes")
	@KafkaListener(topics = "${app.kafka.search-preference-triggered.topic}", groupId = "${spring.application.name}")
	public void process(@Payload String payload) {
		Optional.ofNullable(payload).ifPresentOrElse(spt -> {
			deserialize(spt).ifPresent(event -> {
				log.debug("收到通知{}", event);
				SearchPreferenceTriggeredEvent triggered = (SearchPreferenceTriggeredEvent) event.getPayload();
				String searchPreferenceId = triggered.getId();
                AccountService.MockAccountDTO account = accountService.getAccount(triggered.getEmail());
                NotificationDomainEvent<EmailTriggered> emailevent =
                        NotificationDomainEvent.<EmailTriggered>builder()
                        .id(UUID.randomUUID())
                        .type(NotificationEvent.EventType.EMAIL_TRIGGERED.getEventName())
                        .created(Instant.now())
                        .source(NotificationDomainEvent.SOURCE)
                        .payload(new EmailTriggered(account.firstname(), account.lastname(), triggered.getEmail(), triggered.getTitle(), triggered.getAuthor(), NotificationType.SEARCH_PREFERENCE_HIT))
                        .correlationId(event.getId()) 
                        .build();
                this.dispatcher.send((NotificationDomainEvent) emailevent);
			});
		}, () -> {
			throw new IllegalArgumentException("Payload cannot be null");
		});
	}

	private Optional<SearchPreferenceDomainEvent> deserialize(String spt) {
		try {
			return Optional.of(om.readValue(spt, SearchPreferenceDomainEvent.class));
		} catch (IOException e) {
			log.warn("SearchPreferenceTriggeredEvent Listen exsits error: {}", e.getMessage());
		}
		return Optional.empty();
	}
}
