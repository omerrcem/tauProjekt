package com.mmi.tauProjekt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mmi.tauProjekt.Entity.Student;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import static com.mmi.tauProjekt.Security.SecurityConstants.SECRET;
import static com.mmi.tauProjekt.Security.SecurityConstants.TOKEN_PREFIX;

@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private StudentList list;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private PriceList plist = new PriceList();

    public StudentController(StudentList list,
                             BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.list = list;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    //Ogrenci kayit yeri
    //Token gerektirmez
    @RequestMapping(value = "/sign-up", method = RequestMethod.POST)
    public void signUp(@RequestBody Student s) {
        s.setPassword(bCryptPasswordEncoder.encode(s.getPassword()));
        list.addStudent(s);
    }

    @RequestMapping(value = "get/", method = RequestMethod.POST)
    public void get( @RequestHeader("Authorization") String token){

        System.out.println(token);

        String user = Jwts.parser()
                .setSigningKey(SECRET.getBytes())
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                .getBody()
                .getSubject();

        System.out.println(user);

    }

    //Ogrenci bilgisini geitrmek için method
    //Gonderilen json dosyası sadece jwt token içermeli
    @RequestMapping(value = "/getInfo/{id}",method = RequestMethod.POST)
    public Student getInfo(@PathVariable(value = "id") String id){
       Student s = list.getStudentWithoutPass(id);
        if (s == null) {
            throw new UsernameNotFoundException(id);
        }

        return s;
    }

    //Odeme methodu
    //gonderen kisi bir json dosyasına token, nereye odedigini ve numarayi yollamali
    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public String pay(@RequestBody payType pt){
        int priceAmount = plist.getPrice(pt.priceId);
        String studentId = pt.studentId;

        if (list.getStudent(studentId).getBalance()>priceAmount){
            list.getStudent(studentId).setBalance( list.getStudent(studentId).getBalance() - priceAmount);
            return "paid successfully";
        }else {
            return "insufficient balance";
        }
    }

    //Para yollama methodu
    //gonderen, gonderilen, ne kadar gonderildigi ve jwt tokeni json içerisine yazılmalı
    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    public String transfer(@RequestBody moneyTransferInfo mti){
        String sender = mti.senderId;
        String receiver = mti.receiverId;
        int amount = mti.amount;

        Student r = list.getStudentWithoutPass(receiver);
        if (r == null) {
            throw new UsernameNotFoundException(receiver);
        }

        if (list.getStudent(sender).getBalance() > amount){
            list.getStudent(sender).setBalance( list.getStudent(sender).getBalance() - amount);
            list.getStudent(receiver).setBalance(list.getStudent(receiver).getBalance() + amount);
        return "transfered successfully";
        }else {
            return "insufficient balance";
        }
    }


    @Bean
    public StudentList list(){
        return list;
    }
}


//Para Transferi bilgilerini saklamak için gecici sinif
class moneyTransferInfo{
    String senderId;
    String receiverId;
    int amount;

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}


//Odeme methodu için gecici sinif
class payType{
    String studentId;
    String priceId;

    public String getStudentId() {
        return studentId;
    }

    public String getPriceId() {
        return priceId;
    }
}

