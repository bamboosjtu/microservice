package me.bamboo.common.account;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonTypeName;

import lombok.Getter;

//public record AccountCreatedEvent(String id, String firstname, String lastname, String email, String gender) {
//
//}

@Getter
@JsonTypeName("AccountCreated")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountCreatedEvent extends AccountEvent{
	
	private Long id;
	private String firstname;
	private String lastname;
	private String email;
	
	@JsonCreator
	public AccountCreatedEvent(Long id, String firstname, String lastname, String email) {
		super(EventType.ACCOUNT_CREATED);
		this.id = id;
		this.firstname = firstname;
		this.lastname = lastname;
		this.email = email;
	}
}