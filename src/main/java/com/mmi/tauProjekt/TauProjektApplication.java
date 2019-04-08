package com.mmi.tauProjekt;

import com.mmi.tauProjekt.Lists.FeedbackList;
import com.mmi.tauProjekt.Lists.RecommendList;
import com.mmi.tauProjekt.Lists.CustomerList;
import com.mmi.tauProjekt.QrCode.CustomerPaymentToken;
import com.mmi.tauProjekt.StartUp.StartMessage;
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

	//Kullanici listesi olusturulur
	@Bean
	public CustomerList customerList(){return new CustomerList();}


	//Odeme islemi icin anlik ozel kod ureten sinif
	@Bean
	public CustomerPaymentToken customerPaymentToken(){return new CustomerPaymentToken();}

	@Bean
	public RecommendList recommendList(){return new RecommendList();}

	@Bean
	public FeedbackList feedbackList(){return new FeedbackList();}



	public static void main(String[] args) {
		System.out.println(StartMessage.welcomeMessage());
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
