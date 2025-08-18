package me.bamboo.common.search_preference;

import java.beans.ConstructorProperties;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Getter;

@Getter
@JsonTypeName("SearchPreferenceTriggered")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchPreferenceTriggeredEvent extends SearchPreferenceEvent {
	private String id;

    @JsonCreator
    @ConstructorProperties({"id"})
    public SearchPreferenceTriggeredEvent(String id) {
        super(EventType.SEARCH_PREFERENCE_TRIGGERED);
        this.id = id;
    }

	@Override
	public String toString() {
		return "SearchPreferenceTriggeredEvent [id=" + id + ", eventType=" + eventType + "]";
	}

}
