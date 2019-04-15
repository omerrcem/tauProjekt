package com.mmi.tauProjekt.Security;

import com.mmi.tauProjekt.Entity.LoginSession;
import com.mmi.tauProjekt.Repository.LoginSessionRepository;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.mmi.tauProjekt.Security.SecurityConstants.HEADER_STRING;
import static com.mmi.tauProjekt.Security.SecurityConstants.SECRET;
import static com.mmi.tauProjekt.Security.SecurityConstants.TOKEN_PREFIX;

public class JWTAuthorizationFilter extends BasicAuthenticationFilter {

    public JWTAuthorizationFilter(AuthenticationManager authManager, LoginSessionRepository loginSessionRepository) {
        super(authManager);
        this.loginSessionRepository=loginSessionRepository;
    }
    @Autowired
    private LoginSessionRepository loginSessionRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest req,
                                    HttpServletResponse res,
                                    FilterChain chain) throws IOException, ServletException {
        String header = req.getHeader(HEADER_STRING);

        if (header == null || !header.startsWith(TOKEN_PREFIX)) {
            chain.doFilter(req, res);


            return;
        }

        UsernamePasswordAuthenticationToken authentication = getAuthentication(req);

        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(req, res);
    }

    //Burada gelen tokenlar dogru mu degil mi diye kontrol edilir
    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {

        //Gelen requestteki token alinir
        String token = request.getHeader(HEADER_STRING);

        if (token != null) {
            // parse the token.
            String user = Jwts.parser()
                    .setSigningKey(SECRET.getBytes())
                    .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                    .getBody()
                    .getSubject();

            if (user != null) {

                LoginSession currentSession = loginSessionRepository.findById(user).orElse(new LoginSession());

                if (currentSession.getCustomerId() == null ){
                    log("Unauthorized",request,user);
                    return null;
                }

                if (!currentSession.getLoginToken().equals(token)){
                    System.out.println("User already logged in somewhere else");
                    return null;
                }


                System.out.println("Authorized request: " + user + "  " +request.getRemoteHost()
                        + " " + request.getRequestURL() + " " +new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
                        .format(Calendar.getInstance().getTime()));


                return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());

            }

            return null;

        }

        return null;
    }



    public void log (String requestType, HttpServletRequest req, String user){
        System.out.println(requestType+" request: " + user + "  " +req.getRemoteHost()
                + " " + req.getRequestURL() + " " +new SimpleDateFormat("yyyy.MM.dd HH:mm:ss")
                .format(Calendar.getInstance().getTime()));
    }
}