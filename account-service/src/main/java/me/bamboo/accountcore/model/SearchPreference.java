package me.bamboo.accountcore.model;

import java.math.BigDecimal;

import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import me.bamboo.common.book.Booktype;

@Entity
@Table(name = "search-preference")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchPreference {
	
	@Id
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
	@SequenceGenerator(name = "sequenceGenerator")	
	private Long id;
	
	private String email;
	
	private String title;
	
	@Embedded
	private Criteria criteria;	
	
	public record Criteria(String author, BigDecimal minPrice, BigDecimal maxPrice, Booktype[] types) {
		
	}

}
