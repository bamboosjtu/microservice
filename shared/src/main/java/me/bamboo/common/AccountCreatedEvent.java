package me.bamboo.common;

public record AccountCreatedEvent(String id, String firstname, String lastname, String email, String gender) {

}
