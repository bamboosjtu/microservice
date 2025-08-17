package me.bamboo.common.book;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.NonNull;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        visible = true,
        property = "eventType"
)
public abstract class BookEvent {
	@NonNull
    protected String eventType;

    public BookEvent(BookEvent.EventType eventType) {
        this.eventType = eventType.getEventName();
    }

    private BookEvent() {
    }

    @JsonIgnore
    public String getEventName() {
        return this.eventType;
    }

    public enum EventType {
        BOOK_CREATED("BookCreated");

        private final String eventName;

        EventType(String eventName) {
            this.eventName = eventName;
        }

        public String getEventName() {
            return this.eventName;
        }
    }
    
//    protected static class DefaultBookEvent extends BookEvent {
//        protected DefaultBookEvent() {
//        }
//
//        public boolean equals(Object o) {
//            if (o == this) {
//                return true;
//            } else if (!(o instanceof DefaultBookEvent)) {
//                return false;
//            } else {
//                DefaultBookEvent other = (DefaultBookEvent)o;
//                if (!other.canEqual(this)) {
//                    return false;
//                } else {
//                    return super.equals(o);
//                }
//            }
//        }
//
//        protected boolean canEqual(Object other) {
//            return other instanceof DefaultBookEvent;
//        }
//
//        public int hashCode() {
//            int result = super.hashCode();
//            return result;
//        }
//    }
}
