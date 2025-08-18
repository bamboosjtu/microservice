package me.bamboo.common.search_preference;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import me.bamboo.common.base.DomainEvent;

@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class SearchPreferenceDomainEvent<T extends SearchPreferenceEvent> extends DomainEvent<T> {
	
	public static final String SOURCE = "search-preference";
	
	@JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type"
    )
    @JsonSubTypes({@JsonSubTypes.Type(
            name = "SearchPreferenceCreated",
            value = SearchPreferenceCreatedEvent.class
    ),@JsonSubTypes.Type(
            name = "SearchPreferenceTriggered",
            value = SearchPreferenceTriggeredEvent.class
    )})
    public T getPayload() {
        return super.getPayload();
    }


}
