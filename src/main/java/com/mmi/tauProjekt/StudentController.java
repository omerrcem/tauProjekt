package com.mmi.tauProjekt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.WriterException;
import com.mmi.tauProjekt.Entity.Student;
import com.mmi.tauProjekt.QrCode.QrCodeGenerator;
import com.mmi.tauProjekt.QrCode.UserPaymentToken;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

import static com.mmi.tauProjekt.Security.SecurityConstants.SECRET;
import static com.mmi.tauProjekt.Security.SecurityConstants.TOKEN_PREFIX;



@RestController
@RequestMapping("/students")
public class StudentController {
    @Autowired
    private StudentList list;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private PriceList plist = new PriceList();
    private UserPaymentToken userPaymentToken;

    public StudentController(StudentList list,
                             BCryptPasswordEncoder bCryptPasswordEncoder, UserPaymentToken userPaymentToken) {
        this.list = list;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.userPaymentToken = userPaymentToken;
    }


    //Ogrenci kayit yeri
    //Token gerektirmez
    @RequestMapping(value = "/sign-up", method = RequestMethod.POST)
    public void signUp(@RequestBody Student s) throws CustomException{
    if (list.getStudent(s.getId()) != null){
        throw new CustomException("StudentAlreadyExist");
    }

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
    public String pay(@RequestBody PayType pt, @RequestHeader("Authorization") String token) throws CustomException {
        int priceAmount = plist.getPrice(pt.priceId);
            if (priceAmount == -1){
                return "price not found";
            }
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
        int amount = moneyInfo.getAmount();
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
    public String transfer(@RequestBody moneyTransferInfo mti, @RequestHeader("Authorization") String token) throws CustomException {
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


    //Odeme QrCode uretmek icin method
    //TODO: Aybuke uuid generator ile birlestirilecek
    //Su anda bir qrkod resmini xgeri dondurur
    //
    @RequestMapping(value = "/request-qr-code", method = RequestMethod.POST,  produces = MediaType.IMAGE_JPEG_VALUE)
    private ResponseEntity<byte[]> getQrCode(@RequestHeader("Authorization") String token, HttpServletResponse res) throws IOException, WriterException {
        String studentId = tokenToStudentIdParser(token);
        String qrCode = userPaymentToken.getPaymentToken(studentId);

        File file = new File("./MyQRCode"+studentId+".png");
        QrCodeGenerator.generateQRCodeImage(qrCode,350,350,"./MyQRCode"+studentId+".png");


        byte[] image = Files.readAllBytes(file.toPath());
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.IMAGE_PNG);
        headers.setContentLength(image.length);
        return new ResponseEntity<>(image, headers, HttpStatus.OK);

    }


/*
    @RequestMapping(value = "/send-request-code", method = RequestMethod.POST)
    private String sendQrCode(@RequestBody QrCodeJsonParser qrCodeJsonParser){


    }
*/



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
    int amount;

    public int getAmount() {
        return amount;
    }
}

//Sifre degistirmek icin gecici sinif
class PasswordInfo{
    String newPass;

    public String getNewPass(){
        return newPass;
    }
}

//Barkod Okuyucudan gelen kodu almak icin sinif
class QrCodeJsonParser{
    String qrCode;

    public QrCodeJsonParser(String qrCode){
        this.qrCode = qrCode;
    }

    public String getQrCode() {
        return qrCode;
    }


}
