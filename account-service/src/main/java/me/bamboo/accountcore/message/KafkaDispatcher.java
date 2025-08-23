package me.bamboo.accountcore.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bamboo.common.account.AccountDomainEvent;
import me.bamboo.common.notification.NotificationDomainEvent;
import me.bamboo.common.search_preference.SearchPreferenceDomainEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaDispatcher {
	
	@Autowired
	private final KafkaTemplate<String, String> template;
	@Autowired
	private final ObjectMapper om;
	
	@Value("${app.kafka.account.topic}")
	private String accountTopic;
	
	@Value("${app.kafka.search-preference.topic}")
	private String searchPreferenceTopic;
	
	@Value("${app.kafka.notification.topic}")
	private String notificationTopic;


	public void send(AccountDomainEvent accountCreatedEvent) {
		String event = serialize(accountCreatedEvent);
		this.template.send(accountTopic, event);	
		log.debug("发送消息： {} to {}", event, accountTopic);
	}
	
	public void send(SearchPreferenceDomainEvent searchPreferenceCreatedEvent) {
		String event = serialize(searchPreferenceCreatedEvent);
		this.template.send(searchPreferenceTopic, event);
		log.debug("发送消息： {} to {}", event, searchPreferenceTopic);
	}
	
	public void send(NotificationDomainEvent emailTriggeredEvent) {
		String event = serialize(emailTriggeredEvent);
		this.template.send(notificationTopic, event);
		log.debug("发送消息： {} to {}", event, notificationTopic);
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
