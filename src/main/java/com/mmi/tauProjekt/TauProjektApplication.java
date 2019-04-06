package com.mmi.tauProjekt;

import com.mmi.tauProjekt.QrCode.CustomerPaymentToken;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

//Uygulama buradan baslar

@SpringBootApplication
public class TauProjektApplication {

	//Sifreleri crypt yapmak icin gereken sinif olusturulur
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	//Kullanici listesi olusturulur
	@Bean
	public CustomerList customerList(){return new CustomerList();}


	//Odeme islemi icin anlik ozel kod ureten sinif
	@Bean
	public CustomerPaymentToken customerPaymentToken(){return new CustomerPaymentToken();}







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
