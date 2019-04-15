package com.mmi.tauProjekt.Security;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmi.tauProjekt.Entity.Customer;
import com.mmi.tauProjekt.Entity.LoginSession;
import com.mmi.tauProjekt.Repository.LoginSessionRepository;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.mmi.tauProjekt.Security.SecurityConstants.*;

public class JWTAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private AuthenticationManager authenticationManager;
    @Autowired
    private LoginSessionRepository loginSessionRepository;

    public JWTAuthenticationFilter(AuthenticationManager authenticationManager, LoginSessionRepository loginSessionRepository) {
        this.authenticationManager = authenticationManager;
        this.loginSessionRepository = loginSessionRepository;
    }



    //Kullanici burada yetkilendirilir
    @Override
    public Authentication attemptAuthentication(HttpServletRequest req,
                                                HttpServletResponse res) throws AuthenticationException {
        try {
            Customer creds = new ObjectMapper()
                    .readValue(req.getInputStream(), Customer.class);


            return authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            creds.getId(),
                            creds.getPassword(),
                            new ArrayList<>())
            );
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    //Basarili yetkilendirme sonucunda token sifrelenir ve geri gonderilir
    @Override
    protected void successfulAuthentication(HttpServletRequest req,
                                            HttpServletResponse res,
                                            FilterChain chain,
                                            Authentication auth) throws IOException, ServletException {

        String customerId = ((User) auth.getPrincipal()).getUsername();
        Date loginTime = new Date();
        String ipAddress = req.getRemoteAddr();

        //Konsola bilgi girilir
        System.out.println("User logged in: " + customerId + " " +
                new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
                .format(Calendar.getInstance().getTime()));

        //Token olusturulur
        JwtBuilder token = Jwts.builder()
                .setSubject(((User) auth.getPrincipal()).getUsername())
                //.setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) //Tokenlar suresiz gecerli
                .setId(new Date().toString())
                .signWith(SignatureAlgorithm.HS512, SECRET.getBytes());
        res.addHeader(HEADER_STRING, TOKEN_PREFIX + token.compact());

        //Login sessionu database'e kaydedilir
        loginSessionRepository.save(new LoginSession(customerId,TOKEN_PREFIX + token.compact(),loginTime,ipAddress));
    }
}
