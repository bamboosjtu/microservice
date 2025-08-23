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
        Long created = this.service.save(dto);
        return ResponseEntity.ok().body(created);        
    }

    @PostMapping("/bulk")
    public ResponseEntity<Long> save(@RequestBody SearchPreferenceDTO[] dtos) {
        Long count = 0L;
        for(SearchPreferenceDTO dto: dtos) {
        	this.service.save(dto);
        	count++;
        }
        return ResponseEntity.ok().body(count);        
    }

}
