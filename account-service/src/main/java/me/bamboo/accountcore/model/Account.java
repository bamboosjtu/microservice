package me.bamboo.accountcore.model;

import org.apache.commons.csv.CSVRecord;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "account")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

	@Id
    private Long id;
	
	@NotBlank
    private String firstname;
	
	@NotBlank
    private String lastname;
	
	@NotBlank
    private String email;
    
    public static Account buildFromCsv(CSVRecord record) {
    	return Account.builder()
    			.id(Long.valueOf(record.get("id")))
    			.firstname(record.get("first_name"))
    			.lastname(record.get("last_name"))
    			.email(record.get("email"))
    			.build();
    }
}
