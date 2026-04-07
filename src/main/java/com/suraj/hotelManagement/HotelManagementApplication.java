package com.suraj.hotelManagement;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@EnableKafka
@SpringBootApplication
public class HotelManagementApplication {

	public static void main(String[] args) {


		SpringApplication.run(HotelManagementApplication.class, args);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

		String raw = "Admin123";
		String encoded = "$2a$10$te6aD/5iSxeaOEwsP.layO3pm9OAf3n08y4JYGel1cFl66M7ERcMa";

		//System.out.println(encoder.matches(raw, encoded));
		//Cust123
		//Admin123
		//Recep123
		//alex123



		String code_verifier = "abc123";
		String code_challenge =Integer.toHexString("abc123".hashCode());
		System.out.println(code_challenge+"  this is code challenge");



		String rawPassword = "alex123";
		String encodedPassword = encoder.encode(rawPassword);

		//System.out.println(encodedPassword);
	}

}
