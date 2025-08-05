package me.bamboo.accountcore.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bamboo.common.AccountCreatedEvent;
import me.bamboo.common.SearchPreferenceCreatedEvent;

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
	private String SearchPreferenceTopic;


	public void send(AccountCreatedEvent accountCreatedEvent) {
		String event = serialize(accountCreatedEvent);
		this.template.send(accountTopic, event);	
	}
	
	public void send(SearchPreferenceCreatedEvent searchPreferenceCreatedEvent) {
		String event = serialize(searchPreferenceCreatedEvent);
		this.template.send(SearchPreferenceTopic, event);
		
	}
	
	private String serialize(Object event) {
		try {
			return this.om.writeValueAsString(event);
		} catch (JsonProcessingException e) {
			log.warn("Event {} exsits error.", e.getMessage());
		}
		return "Event Error";
	}
}
