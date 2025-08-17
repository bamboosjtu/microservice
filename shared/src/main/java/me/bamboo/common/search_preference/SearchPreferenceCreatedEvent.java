package me.bamboo.common.search_preference;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Getter;
import me.bamboo.common.book.Booktype;

@Getter
@JsonTypeName("SearchPreferenceCreated")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchPreferenceCreatedEvent extends SearchPreferenceEvent{
	private String id;
	private String title; 
	private String email;
	private String author;
	private BigDecimal minPrice;
	private BigDecimal maxPrice;
	private Booktype[] types;
	
	@JsonCreator
    @ConstructorProperties({"id", "title", "author", "minimumPrice", "maximumPrice", "types"})
    public SearchPreferenceCreatedEvent(String id, String title, String author, BigDecimal minPrice, BigDecimal maxPrice, Booktype[] types) {
        super(EventType.SEARCH_PREFERENCE_CREATED);
        this.id = id;
        this.title = title;
        this.author = author;
        this.minPrice = minPrice;
        this.maxPrice = maxPrice;
        this.types = types;
    }

}
