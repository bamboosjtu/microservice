package me.bamboo.accountcore.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import me.bamboo.accountcore.model.SearchPreference;

public interface SearchPreferenceRepository extends JpaRepository<SearchPreference, Long> {

}
