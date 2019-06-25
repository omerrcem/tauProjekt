package com.mmi.tauProjekt;

import com.google.common.reflect.TypeResolver;
import com.mmi.tauProjekt.QrCode.CustomerPaymentToken;
import com.mmi.tauProjekt.StartUp.StartMessage;
import org.apache.catalina.connector.Connector;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

//Uygulama buradan baslar

@SpringBootApplication
public class TauProjektApplication {

	//Sifreleri crypt yapmak icin gereken sinif olusturulur
	@Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder() {
		return new BCryptPasswordEncoder();
	}

	//Odeme islemi icin anlik ozel kod ureten sinif
	@Bean
	public CustomerPaymentToken customerPaymentToken(){return new CustomerPaymentToken();}



	public static void main(String[] args) {
		System.out.println(StartMessage.welcomeMessage());
		SpringApplication.run(TauProjektApplication.class, args);
	}


    @Bean
    public ServletWebServerFactory servletContainer() {

        TomcatServletWebServerFactory  tomcat = new TomcatServletWebServerFactory();

        Connector ajpConnector = new Connector("AJP/1.3");
        //ajpConnector.setProtocol("AJP/1.3");
        ajpConnector.setPort(9090);
        ajpConnector.setSecure(false);
        ajpConnector.setAllowTrace(false);
        ajpConnector.setScheme("http");
        tomcat.addAdditionalTomcatConnectors(ajpConnector);

        return tomcat;
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
