package com.mmi.tauProjekt;

import com.mmi.tauProjekt.Entity.Admin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;

public class AdminList {
    @Autowired
    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    ArrayList<Admin> adminList = new ArrayList<>(Arrays.asList(

            new Admin("1234", "Omercem","omercemturan@gmail.com",bCryptPasswordEncoder.encode("pass"))
    ));





}
