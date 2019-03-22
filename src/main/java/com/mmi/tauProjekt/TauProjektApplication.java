package com.mmi.tauProjekt;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//Uygulama buradan baslar

@SpringBootApplication
public class TauProjektApplication {

	//Sifreleri crypt yapmak icin gereken sinif olusturulur
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	//Ogrenci listesi olusturulur
	@Bean
	public StudentList studentList(){return new StudentList();}

	//Admin listesi burada oluşturulur
	@Bean
	public AdminList adminList(){return new AdminList();}

	public static void main(String[] args) {
		SpringApplication.run(TauProjektApplication.class, args);
	}

}

/*
//
//  Bean annotationlari siniflar arasi objelere baglamak icin varlar dersek cok yanilmamis oluruz
//	bir sinifta bean olusturup obur siniftan autowire ile baglanabilirsiniz
//	neden mi, cunku spring rocks!!!!
//
//
 */
