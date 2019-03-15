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


    public void addAdmin(Admin s){
        adminList.add(s);
    }

    //Admin bulup getirir
    public Admin getAdmin(String id){
        for (Admin s: adminList) {
            if (s.getAdminId().equals(id)){
                return s;
            }
        }
        return null;
    }

    //Bu fonksiyon clienta gonderilecek olan admin json dosyasnin sifre icermemesi icin
    //tamamen guvenlik icin
    public Admin getAdminWithoutPass(String id){
        for (Admin s: adminList) {
            if (s.getAdminId().equals(id)){
                Admin message = new Admin();
                message.setName(s.getName());
                message.setPassword("");
                message.setAdminId(s.getAdminId());
                message.setMail(s.getMail());
                return message;
            }
        }
        return null;
    }


}
