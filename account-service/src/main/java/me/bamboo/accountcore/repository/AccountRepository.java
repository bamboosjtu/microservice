package me.bamboo.accountcore.repository;

import me.bamboo.accountcore.model.Account;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AccountRepository extends JpaRepository<Account, Long> {

	Optional<Account> findAccountByEmail(String email);
}
