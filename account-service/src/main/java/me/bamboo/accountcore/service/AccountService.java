package me.bamboo.accountcore.service;

import java.io.BufferedReader;
import java.io.FileReader;

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
		try (BufferedReader reader = new BufferedReader(new FileReader(resource.getFile()));
				CSVParser parser = CSVFormat.DEFAULT.withDelimiter(',').withHeader().parse(reader);) {
			parser.stream().map(Account::buildFromCsv).forEach(acc -> {
				Account saveAccount = accountRepo.save(acc);
				System.out.println(saveAccount);
			});
		} catch (Exception e) {
			// TODO Auto-generated catch block
			log.error(e.getMessage());
		}

	}

	public record MockAccountDTO(Long id, String firstname, String lastname, String email, String gender) {
	}

	public MockAccountDTO getAccount(Long id) {
		this.accountRepo.findById(id).ifPresentOrElse(
				acc -> new MockAccountDTO(acc.getId(), acc.getFirstname(), acc.getLastname(), acc.getEmail(), acc.getGender()), () -> {
					throw new EntityNotFoundException("Account hasn't been found with id " + id);
				});
		return null;
	}

}
