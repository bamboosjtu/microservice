package me.bamboo.common;

import java.math.BigDecimal;

public record SearchPreferenceCreatedEvent(Long id, String title, String email, String author, BigDecimal minPrice, BigDecimal maxPrice, Booktype[] types) {

}
