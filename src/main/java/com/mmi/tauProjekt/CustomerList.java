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
            new Customer("160503133","Ömercem Turan","omercemturan@gmail.com",bCryptPasswordEncoder.encode("pass"),"Student"),
            new Customer("160503134","Alp Akyüz","e160503134@stud.tau.edu.tr",bCryptPasswordEncoder.encode("pass"),"Student"),
            new Customer("160501101","Kübra Üstün","e160501101@stud.tau.edu.tr",bCryptPasswordEncoder.encode("pass"),"Student"),
            new Customer("160503117","Gamze Fıçı","gamzef883@gmail.com",bCryptPasswordEncoder.encode("pass"),"Student"),
            new Customer("160503129","Beyza Patlican","e160503129@stud.tau.edu.tr",bCryptPasswordEncoder.encode("pass"),"Student"),
            new Customer("170503101","Sedat Hatip","e170503101@stud.tau.edu.tr",bCryptPasswordEncoder.encode("pass"),"Student"),
            new Customer("160503139","Nusret Özateş","e160503139@stud.tau.edu.tr",bCryptPasswordEncoder.encode("pass"),"Student"),
            new Customer("170503104","Muhammed Zahid Bozkuş","e170503104@stud.tau.edu.tr",bCryptPasswordEncoder.encode("pass"),"Student")

    ));

    public CustomerList(){
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
                message.setBalanceMensa(s.getBalanceMensa());
                message.setBalanceShuttle(s.getBalanceShuttle());
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
