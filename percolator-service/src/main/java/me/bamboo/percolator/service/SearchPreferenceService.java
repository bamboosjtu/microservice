package me.bamboo.percolator.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import me.bamboo.common.BookCreatedEvent;
import me.bamboo.common.SearchPreferenceCreatedEvent;

@Slf4j
@Service
public class SearchPreferenceService {

	@Autowired
	private ElasticsearchOperations operations;

	public void createPercolatorQuery(SearchPreferenceCreatedEvent spc) {
		log.info("Percollator Query saving process has been starting with {}.", spc.toString());		
	}
	
	public void createBookQuery(BookCreatedEvent spc) {
		log.info("Percollator Query saving process has been starting with {}.", spc.toString());		
	}

}
