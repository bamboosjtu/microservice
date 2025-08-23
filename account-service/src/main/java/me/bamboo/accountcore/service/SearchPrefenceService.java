package me.bamboo.accountcore.service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import me.bamboo.accountcore.controller.SearchPreferenceDTO;
import me.bamboo.accountcore.message.KafkaDispatcher;
import me.bamboo.accountcore.model.SearchPreference;
import me.bamboo.accountcore.model.SearchPreference.Criteria;
import me.bamboo.accountcore.repository.SearchPreferenceRepository;
import me.bamboo.common.book.Booktype;
import me.bamboo.common.search_preference.SearchPreferenceCreatedEvent;
import me.bamboo.common.search_preference.SearchPreferenceDomainEvent;
import me.bamboo.common.search_preference.SearchPreferenceEvent;

@Service
@Slf4j
@Transactional(transactionManager = "transactionManager")
public class SearchPrefenceService {
	@Autowired
	private SearchPreferenceRepository repository;
	@Autowired
	private KafkaDispatcher dispatcher;

	public Long save(SearchPreferenceDTO dto) {
		SearchPreference searchPreference = SearchPreference.builder().email(dto.email()).title(dto.title())
				.criteria(new SearchPreference.Criteria(dto.author(), dto.minPrice(), dto.maxPrice(), dto.types()))
				.build();
		SearchPreference saved = this.repository.save(searchPreference);
		log.debug("数据库写入：SearchPreference {}", saved);
		Criteria criteria = saved.getCriteria();
		var event = SearchPreferenceDomainEvent.<SearchPreferenceCreatedEvent>builder()
				.id(UUID.randomUUID())
                .type(SearchPreferenceEvent.EventType.SEARCH_PREFERENCE_CREATED.getEventName())
                .created(Instant.now())
                .source(SearchPreferenceDomainEvent.SOURCE)
                .payload(new SearchPreferenceCreatedEvent(saved.getId().toString(), saved.getEmail(), saved.getTitle(), criteria.author(), criteria.minPrice(), criteria.maxPrice(), criteria.types()))
                .build();
		this.dispatcher.send((SearchPreferenceDomainEvent) event);
		return saved.getId();
	}

	public SearchPreferenceDTO getSearchPreference(String searchPreferenceId) {
		SearchPreference sp = this.repository.findById(Long.valueOf(searchPreferenceId)).orElseThrow(() -> {
			throw new EntityNotFoundException("SearchPreference entity with id " + searchPreferenceId + " hasn't been found.");
		});
		return new SearchPreferenceDTO(sp.getEmail(), sp.getTitle(), sp.getCriteria().author(),
                sp.getCriteria().minPrice(), sp.getCriteria().maxPrice(), sp.getCriteria().types());
	};

}