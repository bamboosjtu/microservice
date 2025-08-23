package me.bamboo.common.notification;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import me.bamboo.common.base.DomainEvent;

@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class NotificationDomainEvent<T extends NotificationEvent> extends DomainEvent<T>{
	public static final String SOURCE = "notification-service";
	
	@JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type"
    )
    @JsonSubTypes({@JsonSubTypes.Type(
            name = "EmailTriggered",
            value = EmailTriggered.class
    )})
    public T getPayload() {
        return super.getPayload();
    }
	

}
