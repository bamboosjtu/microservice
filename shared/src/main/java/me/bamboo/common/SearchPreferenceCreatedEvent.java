package me.bamboo.common;

import java.math.BigDecimal;

public record SearchPreferenceCreatedEvent(String id, String title, String email, String author, BigDecimal minPrice, BigDecimal maxPrice, Booktype[] types) {

}
