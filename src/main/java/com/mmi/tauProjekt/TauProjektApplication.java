package com.mmi.tauProjekt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class TauProjektApplication {


	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public StudentList studentList(){return new StudentList();}

	public static void main(String[] args) {
		SpringApplication.run(TauProjektApplication.class, args);
	}

}
