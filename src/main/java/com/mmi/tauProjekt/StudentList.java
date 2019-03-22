package com.mmi.tauProjekt;

import com.mmi.tauProjekt.Entity.Student;
import com.mmi.tauProjekt.Security.WebSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;


//SQL Baglaninca silinecek
//Ogrencilerin saklandigi sinif

public class StudentList {
@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    ArrayList<Student> list = new ArrayList(Arrays.asList(
            new Student("160503133","omercem","omercemturan@gmail.com",bCryptPasswordEncoder.encode("pass")),
            new Student("160503134", "alp","alp@gmail.com",bCryptPasswordEncoder.encode("pass123"))
    ));

    public StudentList() {
    }


    public void addStudent(Student s){
        list.add(s);
    }

    //Ogrenci bulup getirir
    public Student getStudent(String id){
        for (Student s: list) {
            if (s.getId().equals(id)){
                return s;
            }
        }
        return null;
    }

    //Bu fonksiyon clienta gonderilecek olan ogrenci json dosyasnin sifre icermemesi icin
    //tamamen guvenlik icin
    public Student getStudentWithoutPass(String id){
        for (Student s: list) {
            if (s.getId().equals(id)){
                Student message = new Student();
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

    public void deleteStudent(String id){
        for (Student s: list){
            if (s.getId().equals(id)){
                list.remove(s);
            }
        }

    }


}
