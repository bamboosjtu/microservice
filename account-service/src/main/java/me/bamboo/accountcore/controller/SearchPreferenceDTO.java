package me.bamboo.accountcore.controller;

import java.math.BigDecimal;

import me.bamboo.common.Booktype;

public record SearchPreferenceDTO(String title, String email, String author, BigDecimal minPrice, BigDecimal maxPrice, Booktype[] types) {

}
