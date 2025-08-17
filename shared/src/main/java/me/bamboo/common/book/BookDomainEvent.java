package me.bamboo.common.book;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import me.bamboo.common.base.DomainEvent;

@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class BookDomainEvent<T extends BookEvent> extends DomainEvent<T> {
	public static final String SOURCE = "book-core";

    @JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type"
    )
    @JsonSubTypes({@JsonSubTypes.Type(
            name = "BookCreated",
            value = BookCreatedEvent.class
    )})
    public T getPayload() {
        return super.getPayload();
    }
	

}
