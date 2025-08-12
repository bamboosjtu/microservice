package me.bamboo.percolator.model;

import lombok.Getter;

@Getter
public enum PercolatorIndexFields {
	BOOK_TYPE("bookType", "keyword"),
	PRICE("price", "double"),
	QUERY("query", "percolator");
	
	private String fieldName;
	private String fieldValue;
	
	PercolatorIndexFields(String fieldName, String fieldValue) {
		this.fieldName = fieldName;
		this.fieldValue = fieldValue;
	}

}
