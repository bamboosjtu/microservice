package me.bamboo.common.search_preference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.NonNull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        visible = true,
        property = "eventType"
)
@JsonSubTypes({
		@JsonSubTypes.Type(value=SearchPreferenceCreatedEvent.class, name="SearchPreferenceCreated"),
		@JsonSubTypes.Type(value=SearchPreferenceTriggeredEvent.class, name="SearchPreferenceTriggered")})
public abstract class SearchPreferenceEvent {
	@NonNull
    protected String eventType;

    public SearchPreferenceEvent(SearchPreferenceEvent.EventType eventType) {
        this.eventType = eventType.getEventName();
    }

    private SearchPreferenceEvent() {
    }

    @JsonIgnore
    public String getEventName() {
        return this.eventType;
    }

    public static enum EventType {
        SEARCH_PREFERENCE_CREATED("SearchPreferenceCreated"),
        SEARCH_PREFERENCE_TRIGGERED("SearchPreferenceTriggered");

        private final String eventName;

        EventType(String eventName) {
            this.eventName = eventName;
        }

        public String getEventName() {
            return this.eventName;
        }
    }

//    protected static class DefaultSearchPreferenceEvent extends SearchPreferenceEvent {
//        protected DefaultSearchPreferenceEvent() {
//        }
//
//        public boolean equals(Object o) {
//            if (o == this) {
//                return true;
//            } else if (!(o instanceof DefaultSearchPreferenceEvent)) {
//                return false;
//            } else {
//                DefaultSearchPreferenceEvent other = (DefaultSearchPreferenceEvent)o;
//                if (!other.canEqual(this)) {
//                    return false;
//                } else {
//                    return super.equals(o);
//                }
//            }
//        }
//
//        protected boolean canEqual(Object other) {
//            return other instanceof DefaultSearchPreferenceEvent;
//        }
//
//        public int hashCode() {
//            int result = super.hashCode();
//            return result;
//        }
//    }	

}
