package com.mmi.tauProjekt;

import com.mmi.tauProjekt.Entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private StudentList studentList;
    @Autowired
    private AdminList adminList;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AdminController(StudentList studentList, AdminList adminList, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.studentList = studentList;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }


    //
    //Admin panelinde ogrenci bilgilerini degistirmeye yarar
    //Json icerisinde Student classına uygun bir body olmalı
    //Admin tokeni gerektirir
    //
    @RequestMapping(value = "/edit-student-info", method = RequestMethod.POST)
    public void editStudentInfo(@RequestBody Student targetStudent) {
        Student s =studentList.getStudent(targetStudent.getId());
        if (s == null){
            throw  new UsernameNotFoundException(targetStudent.getId());
        }
        if (!targetStudent.getId().equals("")){
            s.setId(targetStudent.getId());
        }
        if (targetStudent.getBalance()!=-1){
            s.setBalance(targetStudent.getBalance());
        }
        if (!targetStudent.getName().equals("")){
            s.setName(targetStudent.getName());
        }
        if (!targetStudent.getMail().equals("")){
            s.setMail(targetStudent.getMail());
        }
        if(!targetStudent.getPassword().equals("")){
            s.setPassword(bCryptPasswordEncoder.encode(targetStudent.getPassword()));
        }
    }

    //
    //Ogrenci silmeye yarar
    //
    @RequestMapping(value = "/delete-student", method = RequestMethod.POST)
    public void deleteStudent(@RequestBody StudentIdInfo studentIdInfo){
        String id = studentIdInfo.getStudentId();
        if (studentList.getStudent(id) == null){
            throw new UsernameNotFoundException(id);
        }
        studentList.deleteStudent(id);

    }

}

class StudentIdInfo{
    private String studentId;

    public String getStudentId() {
        return studentId;
    }
}


