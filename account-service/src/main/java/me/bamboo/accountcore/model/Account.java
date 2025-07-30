package me.bamboo.accountcore.model;

import org.apache.commons.csv.CSVRecord;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
    private String gender;
    
    public static Account buildFromCsv(CSVRecord record) {
    	Account account = Account.builder()
    			.id(Long.valueOf(record.get("id")))
    			.firstname(record.get("first_name"))
    			.lastname(record.get("last_name"))
    			.email(record.get("email"))
    			.gender(record.get("gender"))
    			.build();
    	System.out.println(account);
    	return account;
    }
}
