package me.bamboo.bookcore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;

import me.bamboo.bookcore.service.BookService;


@RestController
@RequestMapping("/api/v1/book")
@RequiredArgsConstructor
public class BookController {

	private final BookService service;

    @PostMapping
    public ResponseEntity<Long> save(@RequestBody BookDTO dto) {
        Long createdId = service.save(dto);
        return ResponseEntity.ok().body(createdId);        
    }


}
