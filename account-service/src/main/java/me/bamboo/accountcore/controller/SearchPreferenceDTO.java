package me.bamboo.accountcore.controller;

import java.math.BigDecimal;

import me.bamboo.common.book.Booktype;

public record SearchPreferenceDTO(String email, String title, String author, BigDecimal minPrice, BigDecimal maxPrice, Booktype[] types) {

}
