package me.bamboo.percolator.message;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bamboo.common.search_preference.SearchPreferenceDomainEvent;
import me.bamboo.common.search_preference.SearchPreferenceTriggeredEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaDispatcher {
	
	@Autowired
	private final KafkaTemplate<String, String> template;
	@Autowired
	private final ObjectMapper om;

	
	@Value("${app.kafka.search-preference-triggered.topic}")
	private String SearchPreferenceTriggeredEvent;


	
	@SuppressWarnings("rawtypes")
	public void send(SearchPreferenceDomainEvent domainEvent) {
		String event = serialize(domainEvent);
		this.template.send(SearchPreferenceTriggeredEvent, event);
		log.debug("发送消息： {} to {}", event, SearchPreferenceTriggeredEvent);
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
