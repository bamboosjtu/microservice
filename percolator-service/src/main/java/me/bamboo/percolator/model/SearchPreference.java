package me.bamboo.percolator.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

@Document(indexName = "search-preferences")
public class SearchPreference {
	@Id
	private String id;

	@Field(type = FieldType.Percolator)
	private String query;

	@Field(type = FieldType.Keyword)
	private String booktype;

	@Field(type = FieldType.Double)
	private Double price;
}
