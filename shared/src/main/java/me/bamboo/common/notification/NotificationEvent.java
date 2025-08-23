package me.bamboo.common.notification;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.NonNull;
import me.bamboo.common.account.AccountCreatedEvent;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        visible = true,
        property = "eventType"
)
@JsonSubTypes({@JsonSubTypes.Type(EmailTriggered.class)})
public abstract class NotificationEvent {
	@NonNull
    protected String eventType;

    public NotificationEvent(EventType eventType) {
        this.eventType = eventType.getEventName();
    }

    private NotificationEvent() {
    }
    
    public static enum EventType {
        EMAIL_TRIGGERED("EmailTriggered");

        private final String eventName;

        EventType(String eventName) {
            this.eventName = eventName;
        }

        public String getEventName() {
            return this.eventName;
        }
    }

}
