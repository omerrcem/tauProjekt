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




    //Ogrenci bilgisini geitrmek için method
    //Gonderilen json dosyası sadece jwt token içermeli
    //Ogrenci sinfini sifresi silinmis bir sekilde geri dondurur
    @RequestMapping(value = "/get-info",method = RequestMethod.POST)
    public Student getInfo( @RequestHeader("Authorization") String token){
        String studentId = tokenToStudentIdParser(token);
        Student s = list.getStudentWithoutPass(studentId);
        if (s == null) {
            throw new UsernameNotFoundException(studentId);
        }
        return s;
    }




    //Odeme methodu
    //gonderen kisi bir json dosyasına bir jwt token, nereye odedigini
    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public String pay(@RequestBody PayType pt, @RequestHeader("Authorization") String token){
        int priceAmount = plist.getPrice(pt.priceId);
        String studentId = tokenToStudentIdParser(token);
        if (list.getStudent(studentId).getBalance()>priceAmount){
            list.getStudent(studentId).setBalance( list.getStudent(studentId).getBalance() - priceAmount);
            return "paid successfully";
        }else {
            return "insufficient balance";
        }
    }



    //Para yukleme methodu
    //gonderen kisi bir json dosyasinda yuklenecek miktari ve tokeni gondermeli
    @RequestMapping(value = "/deposit", method = RequestMethod.POST)
    public String deopsit(@RequestHeader("Authorization") String token, @RequestBody MoneyInfo moneyInfo){
        int amount = moneyInfo.getAmountMoney();
        String studentId = tokenToStudentIdParser(token);

        Student r = list.getStudent(studentId);
        if (r == null) {
            throw new UsernameNotFoundException(studentId);
        }

        r.setBalance( r.getBalance() + amount);
        return ("deposited successfully");
    }



    //Para yollama methodu
    //gonderilen, ne kadar gonderildigi ve jwt tokeni json içerisine yazılmalı
    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    public String transfer(@RequestBody moneyTransferInfo mti, @RequestHeader("Authorization") String token){
        String sender = tokenToStudentIdParser(token);
        String receiver = mti.getReceiverId();
        int amount = mti.getAmount();

        Student r = list.getStudent(receiver);
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


    //Sifre degistirme methodu
    //Gonderen kisinin json dosyasi icinde jwt token ve yeni sifreyi gondermesi gerekir
    @RequestMapping(value = "/change-password", method = RequestMethod.POST)
    private String changePassword(@RequestBody PasswordInfo passInfo, @RequestHeader("Authorization") String token){
        String newPass = bCryptPasswordEncoder.encode(passInfo.getNewPass());
        String studentId = tokenToStudentIdParser(token);

        Student s = list.getStudent(studentId);
        s.setPassword(newPass);
        return "password changed successfully";
    }



    //token icindeki kullanici adini geri dondurur
    private String tokenToStudentIdParser(String token){
        String user = Jwts.parser()
                .setSigningKey(SECRET.getBytes())
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                .getBody()
                .getSubject();
        return user;
    }

    @Bean
    public StudentList list(){
        return list;
    }
}


//Para Transferi bilgilerini saklamak için gecici sinif
class moneyTransferInfo{
    String receiverId;
    int amount;

    public String getReceiverId() {
        return receiverId;
    }

    public int getAmount() {
        return amount;
    }
}


//Odeme methodu için gecici sinif
class PayType{
    String priceId;

    public String getPriceId() {
        return priceId;
    }
}

//Para yukleme methodu icin gecici sinif
class MoneyInfo{
    int amountMoney;

    public int getAmountMoney() {
        return amountMoney;
    }
}

//Sifre degistirmek icin gecici sinif
class PasswordInfo{
    String newPass;

    public String getNewPass(){
        return newPass;
    }
}
