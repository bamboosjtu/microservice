package me.bamboo.percolator.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.mapping.IndexCoordinates;
import org.springframework.data.elasticsearch.core.query.IndexQuery;
import org.springframework.data.elasticsearch.core.query.IndexQueryBuilder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import co.elastic.clients.elasticsearch._types.FieldValue;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.json.JsonData;
import lombok.extern.slf4j.Slf4j;
import me.bamboo.common.book.BookCreatedEvent;
import me.bamboo.common.search_preference.SearchPreferenceCreatedEvent;

@Slf4j
@Service
public class PercolatorService {
	public static final String PERCOLATOR_INDEX = "search-preferences";

	@Autowired
	private ElasticsearchOperations operations;
	
	@Autowired
	private ObjectMapper objectMapper;  	

	public void save(SearchPreferenceCreatedEvent spc) {
		log.info("Percollator Query saving process has been starting with {}.", spc.toString());

		// 构建布尔查询
		var boolQuery = getBoolQueryBuilder(spc);
		
		// 构建文档：{ "query": { ... } }
		Map<String, Object> source = new HashMap<>();
		source.put("query", boolQuery);
		log.debug("boolQuery is {}", source);
		
		// 创建索引操作
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

	public void findMatches(BookCreatedEvent bookCreatedEvent) {
		log.info("Percollator Query saving process has been starting with {}.", bookCreatedEvent.toString());
	}
}
