package me.bamboo.common.account;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import me.bamboo.common.base.DomainEvent;

@SuperBuilder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountDomainEvent<T extends AccountEvent> extends DomainEvent<T>{
	public static final String SOURCE = "account-core";
	
	@JsonTypeInfo(
            use = JsonTypeInfo.Id.NAME,
            include = JsonTypeInfo.As.EXTERNAL_PROPERTY,
            property = "type"
    )
    @JsonSubTypes({@JsonSubTypes.Type(
            name = "AccountCreated",
            value = AccountCreatedEvent.class
    )})
    public T getPayload() {
        return super.getPayload();
    }
	

}
