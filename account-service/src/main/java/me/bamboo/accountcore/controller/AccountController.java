package me.bamboo.accountcore.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import me.bamboo.accountcore.service.AccountService;


@RestController
@RequestMapping("/api/v1/account")
public class AccountController {

	@Autowired
	private AccountService accountService;
    

    @PostMapping("/create-mock-accounts")
    public ResponseEntity createAccount() {
        int count = this.accountService.mockAccounts();
        return ResponseEntity.ok().body(count);        
    }
    
    @GetMapping( "/{id}")
    public ResponseEntity<AccountService.MockAccountDTO> getAccount(@PathVariable("id") Long id){
    	AccountService.MockAccountDTO dto = this.accountService.getAccount(id);
    	return ResponseEntity.ok().body(dto);
    }


}
