package com.mmi.tauProjekt.Mail;

import javax.mail.*;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Properties;

/**
 *
 * @author Mukuljaiswal
 *
 */


//Otomatik mail gondermek icin implemantasyon


@Service
public class MailService {

    /*
     * The Spring Framework provides an easy abstraction for sending email by using
     * the JavaMailSender interface, and Spring Boot provides auto-configuration for
     * it as well as a starter module.
     */
    private JavaMailSender javaMailSender;

    /**
     * @param javaMailSender
     */
    @Autowired
    public MailService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }



    public void sendEmail(String from, String to, String sub, String cont) throws MailException, MessagingException {

        /*
         * This JavaMailSender Interface is used to send Mail in Spring Boot. This
         * JavaMailSender extends the MailSender Interface which contains send()
         * function. SimpleMailMessage Object is required because send() function uses
         * object of SimpleMailMessage as a Parameter
         */
        Session mailSession = Session.getInstance( new Properties() );
        Transport transport = mailSession.getTransport();


        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, false, "utf-8");

        Multipart multipart = new MimeMultipart( "alternative" );

        String html = cont;
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent( html, "text/html; charset=utf-8" );
        multipart.addBodyPart(htmlPart);

        message.setContent(multipart);
        helper.setTo(to);
        helper.setFrom(from);
        helper.setSubject(sub);

        /*
        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setTo(to);
        mail.setFrom(from);
        mail.setSubject(sub);
        mail.setText(cont);
        */
        /*
         * This send() contains an Object of SimpleMailMessage as an Parameter
         */
        javaMailSender.send(message);
    }
}