package me.bamboo.percolator.service;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.elasticsearch.core.search.Hit;
import co.elastic.clients.json.JsonData;

import lombok.extern.slf4j.Slf4j;

import me.bamboo.common.book.BookCreatedEvent;
import me.bamboo.common.book.BookDomainEvent;
import me.bamboo.common.search_preference.SearchPreferenceCreatedEvent;
import me.bamboo.common.search_preference.SearchPreferenceDomainEvent;
import me.bamboo.common.search_preference.SearchPreferenceEvent;
import me.bamboo.common.search_preference.SearchPreferenceTriggeredEvent;

@Slf4j
@Service
public class PercolatorService {
	public static final String PERCOLATOR_INDEX = "search-preferences";

	@Autowired
	private ElasticsearchOperations operations;
	
	@Autowired
	private ElasticsearchClient client;
	
	@Autowired
	private ObjectMapper objectMapper;  	

	public void save(SearchPreferenceDomainEvent domainEvent) {
		SearchPreferenceCreatedEvent spc = (SearchPreferenceCreatedEvent) domainEvent.getPayload();
		// 构建布尔查询
		var boolQuery = getBoolQueryBuilder(spc);
		
		// 构建文档：{ "query": { ... } }
		Map<String, Object> source = new HashMap<>();
		source.put("query", boolQuery);
	       // 2. 邮箱匹配（email）
		source.put("email", spc.getEmail());
		log.debug("反向查询语句 is {}", source);
		
		// 创建索引操作并执行，PUT /search-preferences/_doc
		try {
			String jsonSource = objectMapper.writeValueAsString(source);
			var indexQuery =  new IndexQueryBuilder()
					.withId(spc.getId())
					.withSource(jsonSource)
					.build();					
			operations.index(indexQuery, IndexCoordinates.of(PERCOLATOR_INDEX));
		} catch (JsonProcessingException e) {
			log.error("Failed to serialize query to JSON", e);
		    throw new RuntimeException("Failed to save percolator query", e);
		}

	}
	
	private Map<String, Object> getBoolQueryBuilder(SearchPreferenceCreatedEvent spc) {		
		
        Map<String, Object> bool = new HashMap<>();
        List<Map<String, Object>> filters = new ArrayList<>();

        // 1. 类型匹配（terms）
        if (spc.getTypes() != null && spc.getTypes().length > 0) {
            Map<String, Object> terms = new HashMap<>();
            terms.put("terms", Collections.singletonMap(
                "booktype",
                Arrays.stream(spc.getTypes()).map(Enum::name).toList()
            ));
            filters.add(terms);
        }

        // 2. 价格范围（range）
        Map<String, Object> rangeClause = new HashMap<>();
        boolean hasRange = false;

        if (spc.getMinPrice() != null) {
            rangeClause.put("gte", spc.getMinPrice().doubleValue());
            hasRange = true;
        }
        if (spc.getMaxPrice() != null) {
            rangeClause.put("lte", spc.getMaxPrice().doubleValue());
            hasRange = true;
        }

        if (hasRange) {
            Map<String, Object> range = new HashMap<>();
            range.put("range", Collections.singletonMap(
                "price", rangeClause
            ));
            filters.add(range);
        }

        if (!filters.isEmpty()) {
            bool.put("filter", filters);
        }

        Map<String, Object> query = new HashMap<>();
        query.put("bool", bool);
        return query;
    }

	public List<SearchPreferenceDomainEvent<SearchPreferenceTriggeredEvent>> findMatches(BookDomainEvent domainEvent) {
		BookCreatedEvent bookCreatedEvent = (BookCreatedEvent) domainEvent.getPayload();
		
		log.debug("文档匹配 has been starting with {}.", bookCreatedEvent.toString());
		
		Map<String, Object> doc = new HashMap<>();
		doc.put("booktype", bookCreatedEvent.getType().name());
		doc.put("price", bookCreatedEvent.getPrice().doubleValue());

		try {
			String jsonString = objectMapper.writeValueAsString(doc);
			SearchRequest request = SearchRequest.of(s -> s
	                .index(PERCOLATOR_INDEX)
	                .query(q -> q
	                        .percolate(p -> p
	                                .field("query")
	                                .document(JsonData.fromJson(jsonString))
	                )
	        ));
			log.debug("书籍匹配查询语句 is {}", request);	
			
			SearchResponse<Map> response = client.search(request, Map.class);
			log.debug("书籍匹配查询结果 is {}", response);	
			log.debug("书籍匹配查询结果 is {}", response.hits().hits());	
			
			List<SearchPreferenceDomainEvent<SearchPreferenceTriggeredEvent>> events = new ArrayList<>();
			for (Hit<Map> hit : response.hits().hits()) {
				try {
		            // 获取 _source 并转成 JSON
		            Map<String, Object> sourceMap = hit.source();
		            if (sourceMap != null) {
		            	var event = SearchPreferenceDomainEvent.<SearchPreferenceTriggeredEvent>builder()
		            		.id(UUID.randomUUID())
		            		.type(SearchPreferenceEvent.EventType.SEARCH_PREFERENCE_TRIGGERED.getEventName())
		            		.correlationId(domainEvent.getId()) //chaining domain events
		            		.created(Instant.now())
		            		.source(SearchPreferenceDomainEvent.SOURCE)
		            		.payload(new SearchPreferenceTriggeredEvent(hit.id(), (String) hit.source().get("email"), bookCreatedEvent.getTitle(), bookCreatedEvent.getAuthor()))//hit.source().get("query")
		            		.build();
		                events.add(event);
		            }
		        } catch (Exception e) {
		            e.printStackTrace();
		        }
			}		
			log.debug("response of SearchPreferenceTriggeredEvents is {}.", events);
			return events;
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		return List.of();

	}
//	String id, String email, String title
}
