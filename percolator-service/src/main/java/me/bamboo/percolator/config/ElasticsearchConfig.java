package me.bamboo.percolator.config;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.indices.CreateIndexResponse;
import co.elastic.clients.elasticsearch.indices.ExistsRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ElasticsearchConfig {
	private static final String INDEX_NAME = "search-preferences";

	@Bean
	public ApplicationRunner createPercolatorIndex(ElasticsearchClient client) {
		return args -> {
			// 检查索引是否存在
			boolean exists = client.indices().exists(ExistsRequest.of(e -> e.index(INDEX_NAME))).value();
			if (!exists) {
				// 创建索引并添加 percolator 字段
				CreateIndexResponse response = client.indices()
						.create(c -> c.index(INDEX_NAME)
								.mappings(m -> m.properties("query", p -> p.percolator(pp -> pp))
										.properties("price", p -> p.double_(tt -> tt))
										.properties("booktype", p -> p.keyword(kk -> kk))
										.properties("email", p -> p.keyword(kk -> kk))));
				log.debug("Percolator索引创建结果: " + response.acknowledged());
			}
		};
	}

}
