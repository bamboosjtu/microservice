package me.bamboo.accountcore.controller;

import me.bamboo.accountcore.model.Account;
import me.bamboo.accountcore.model.SearchPreference;
import me.bamboo.accountcore.repository.AccountRepository;
import me.bamboo.accountcore.repository.SearchPreferenceRepository;
import me.bamboo.accountcore.kafka.EventProducer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class AccountController {

    @Autowired
    private AccountRepository accountRepo;

    @Autowired
    private SearchPreferenceRepository prefRepo;

    @Autowired
    private EventProducer producer;

    @PostMapping("/accounts")
    public Account createAccount(@RequestBody Account account) {
        Account saved = accountRepo.save(account);
        producer.sendAccountCreatedEvent(saved);
        return saved;
    }

    @PostMapping("/preferences")
    public SearchPreference savePreference(@RequestBody SearchPreference preference) {
        SearchPreference saved = prefRepo.save(preference);
        producer.sendPreferenceCreatedEvent(saved);
        return saved;
    }
}
