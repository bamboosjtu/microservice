package me.bamboo.common.search_preference;

import java.beans.ConstructorProperties;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Getter;

@Getter
@JsonTypeName("SearchPreferenceTriggered")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SearchPreferenceTriggeredEvent extends SearchPreferenceEvent {
	private String id;
	private String email;
	private String title; 
	private String author;

    @JsonCreator
    @ConstructorProperties({"id", "email", "title", "author"})
    public SearchPreferenceTriggeredEvent(String id, String email, String title, String author) {
        super(EventType.SEARCH_PREFERENCE_TRIGGERED);
        this.id = id;
        this.email = email;
        this.title = title; 
        this.author = author;
    }

	@Override
	public String toString() {
		return "SearchPreferenceTriggeredEvent [id=" + id + ", email=" + email + ", title=" + title + ", author=" + author + ", eventType=" + eventType + "]";
	}

}
