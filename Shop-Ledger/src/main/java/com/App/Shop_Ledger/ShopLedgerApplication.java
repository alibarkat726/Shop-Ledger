package com.App.Shop_Ledger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@SpringBootApplication
@EnableMongoRepositories
public class ShopLedgerApplication {

	public static void main(String[] args) {
		SpringApplication.run(ShopLedgerApplication.class, args);
	}
}
