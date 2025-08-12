package me.bamboo.common;

import java.math.BigDecimal;

public record BookCreatedEvent(String id,String title, String author, BigDecimal price, Booktype type) {

}
