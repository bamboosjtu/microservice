package me.bamboo.bookcore.controller;

import java.math.BigDecimal;

import me.bamboo.common.Booktype;

public record BookDTO(String title, String author, BigDecimal price, String type) {

}
