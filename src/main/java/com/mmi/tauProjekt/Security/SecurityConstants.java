package com.mmi.tauProjekt.Security;

//Guvenlik degiskenleri

public class SecurityConstants {
    public static final String SECRET = "xdxdxd "; //Gelmis gecmis en iyi sifre
    public static final long EXPIRATION_TIME = 423_000_000; // 5 gün
    public static final String TOKEN_PREFIX = "Bearer ";
    public static final String HEADER_STRING = "Authorization";
    public static final String SIGN_UP_URL = "/customers/sign-up"; //Kayit sayfasi
}