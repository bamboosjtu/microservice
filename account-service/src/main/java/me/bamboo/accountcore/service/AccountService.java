package me.bamboo.accountcore.service;


import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import me.bamboo.accountcore.message.KafkaDispatcher;
import me.bamboo.accountcore.model.Account;
import me.bamboo.accountcore.repository.AccountRepository;
import me.bamboo.common.account.AccountCreatedEvent;

@Service
@Slf4j
@Transactional(transactionManager = "transactionManager")
public class AccountService {
	@Autowired
	private AccountRepository accountRepo;
	@Autowired
	private KafkaDispatcher dispatcher;
	@Autowired
	private ResourceLoader resourceLoader;
	
	public int mockAccounts() {
		Resource resource = resourceLoader.getResource("classpath:data.csv");
		int count = 0; 
		try (InputStream is = resource.getInputStream();
				Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
				CSVParser parser = CSVFormat.DEFAULT.withDelimiter(',').withIgnoreSurroundingSpaces().withFirstRecordAsHeader().parse(reader)) {			
			for (CSVRecord item : parser) {
				Account account = Account.buildFromCsv(item);
				Account savedAccount = accountRepo.save(account);
				log.debug("Account saving process has been finished. {}", savedAccount);
				this.dispatcher.send(new AccountCreatedEvent(savedAccount.getId(), savedAccount.getFirstname(), savedAccount.getLastname(), savedAccount.getEmail()));
				count++;
			}
			log.debug("Total {} account records were created", count);
			return count;
		} catch (Exception e) {
			log.error("Error loading CSV data: {}", e.getMessage(), e);
		}
		return 0;

	}

	public record MockAccountDTO(Long id, String firstname, String lastname, String email) {
	}

	public MockAccountDTO getAccount(Long id) {
		return this.accountRepo.findById(id).map(acc -> new MockAccountDTO(acc.getId(), acc.getFirstname(), acc.getLastname(), acc.getEmail()))
		        .orElseThrow(() -> new EntityNotFoundException("Account hasn't been found with id " + id));

	}
}
