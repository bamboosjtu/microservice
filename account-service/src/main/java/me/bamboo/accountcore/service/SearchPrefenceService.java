package me.bamboo.accountcore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import me.bamboo.accountcore.controller.SearchPreferenceDTO;
import me.bamboo.accountcore.message.KafkaDispatcher;
import me.bamboo.accountcore.model.SearchPreference;
import me.bamboo.accountcore.model.SearchPreference.Criteria;
import me.bamboo.accountcore.repository.SearchPreferenceRepository;
import me.bamboo.common.SearchPreferenceCreatedEvent;

@Service
@Slf4j
@Transactional(transactionManager = "transactionManager")
public class SearchPrefenceService {
	@Autowired
	private SearchPreferenceRepository repository;
	@Autowired
	private KafkaDispatcher dispatcher;

	public Long save(SearchPreferenceDTO dto) {
		SearchPreference searchPreference = SearchPreference.builder().title(dto.title()).email(dto.email())
				.criteria(new SearchPreference.Criteria(dto.author(), dto.minPrice(), dto.maxPrice(), dto.types()))
				.build();
		SearchPreference saved = this.repository.save(searchPreference);
		log.debug("SearchPreference saving process has been finished. {}", saved);
		Criteria criteria = saved.getCriteria();
		this.dispatcher.send(new SearchPreferenceCreatedEvent(saved.getId().toString(), saved.getTitle(), saved.getEmail(),
				criteria.author(), criteria.minPrice(), criteria.maxPrice(), criteria.types()));
		return saved.getId();
	}

}
