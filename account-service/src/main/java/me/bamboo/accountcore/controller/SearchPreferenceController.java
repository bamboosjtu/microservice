package me.bamboo.accountcore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import me.bamboo.accountcore.service.SearchPrefenceService;



@RestController
@RequestMapping("/api/v1/search-preference")
public class SearchPreferenceController {

	@Autowired
	private SearchPrefenceService service;
    

    @PostMapping
    public ResponseEntity<Long> save(@RequestBody SearchPreferenceDTO dto) {
        this.service.save(dto);
        return ResponseEntity.ok().build();        
    }


}
