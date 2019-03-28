package com.mmi.tauProjekt;

import com.mmi.tauProjekt.Entity.Customer;
import com.mmi.tauProjekt.Security.WebSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;


//SQL Baglaninca silinecek
//Ogrencilerin saklandigi sinif

public class CustomerList {
@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    ArrayList<Customer> list = new ArrayList(Arrays.asList(
            new Customer("160503133","omer cem turan","omercemturan@gmail.com",bCryptPasswordEncoder.encode("pass")),
            new Customer("160503134", "alp akyuz","alp@gmail.com",bCryptPasswordEncoder.encode("pass123"))
    ));

    public CustomerList() {
    }


    public void addCustomer(Customer s){
        list.add(s);
    }

    //Ogrenci bulup getirir
    public Customer getCustomer(String id){
        for (Customer s: list) {
            if (s.getId().equals(id)){
                return s;
            }
        }
        return null;
    }

    //Bu fonksiyon clienta gonderilecek olan ogrenci json dosyasnin sifre icermemesi icin
    //tamamen guvenlik icin
    public Customer getCustomerWithoutPass(String id){
        for (Customer s: list) {
            if (s.getId().equals(id)){
                Customer message = new Customer();
                message.setName(s.getName());
                message.setPassword("");
                message.setId(s.getId());
                message.setBalance(s.getBalance());
                message.setMail(s.getMail());
                return message;
            }
        }
        return null;
    }

    public void deleteCustomer(String id){
        for (Customer s: list){
            if (s.getId().equals(id)){
                list.remove(s);
            }
        }

    }


}
