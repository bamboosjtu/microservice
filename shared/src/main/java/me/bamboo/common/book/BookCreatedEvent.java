package me.bamboo.common.book;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@JsonTypeName("BookCreated")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookCreatedEvent extends BookEvent {

	private String id;
	private String title;
	private String author;
	private BigDecimal price;
	private Booktype type;
	
	@JsonCreator
	@ConstructorProperties({"id", "title", "author", "price", "types"})
	public BookCreatedEvent(String id, String title, String author, BigDecimal price, Booktype type) {
		super(EventType.BOOK_CREATED);
		this.id = id;
		this.title = title;
		this.author = author;
		this.price = price;
		this.type = type;
	}
	
	@Override
	public String toString() {
		return "BookCreatedEvent [id=" + id + ", title=" + title + ", author=" + author + ", price=" + price + ", type="
				+ type + "]";
	}

}
