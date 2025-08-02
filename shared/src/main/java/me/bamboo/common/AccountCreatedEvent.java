package me.bamboo.common;

public record AccountCreatedEvent(Long id, String firstname, String lastname, String email, String gender) {

}
