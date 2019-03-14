package com.mmi.tauProjekt;

import com.mmi.tauProjekt.Entity.Student;
import com.mmi.tauProjekt.Security.WebSecurityConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;


public class StudentList {
@Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    ArrayList<Student> list = new ArrayList(Arrays.asList(
            new Student("160503133","omercem","omercemturan@gmail.com",bCryptPasswordEncoder.encode("pass"),10),
            new Student("160503134", "alp","alp@gmail.com",bCryptPasswordEncoder.encode("pass123"),50)
    ));

    public StudentList() {
    }


    public void addStudent(Student s){
        list.add(s);
    }

    public Student getStudent(String id){
        for (Student s: list) {
            if (s.getId().equals(id)){
                return s;
            }
        }
        return null;
    }

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


}
