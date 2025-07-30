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

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

import me.bamboo.accountcore.model.Account;
import me.bamboo.accountcore.repository.AccountRepository;

@Service
@Slf4j
public class AccountService {
	@Autowired
	private AccountRepository accountRepo;

	@Autowired
	private ResourceLoader resourceLoader;

	@Transactional
	public void mockAccounts() {
		Resource resource = resourceLoader.getResource("classpath:data.csv");
		try (InputStream is = resource.getInputStream();
				Reader reader = new InputStreamReader(is, StandardCharsets.UTF_8);
				CSVParser parser = CSVFormat.DEFAULT.withDelimiter(',').withIgnoreSurroundingSpaces().withFirstRecordAsHeader().parse(reader)) {
//			System.out.println("Total records: " + parser.getRecords().size());
			for (CSVRecord record : parser) {
				Account acc = Account.buildFromCsv(record);
				Account saved = accountRepo.save(acc);
				System.out.println(saved);
			}
		} catch (Exception e) {
			log.error("Error loading CSV data: {}", e.getMessage(), e);
		}

	}

	public record MockAccountDTO(Long id, String firstname, String lastname, String email, String gender) {
	}

	public MockAccountDTO getAccount(Long id) {
		return this.accountRepo.findById(id)
				.map(acc -> new MockAccountDTO(acc.getId(), acc.getFirstname(), acc.getLastname(), acc.getEmail(),
						acc.getGender()))
				.orElseThrow(() -> new EntityNotFoundException("Account hasn't been found with id " + id));
	}

}
