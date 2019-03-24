package com.mmi.tauProjekt;

import com.google.zxing.WriterException;
import com.mmi.tauProjekt.Entity.Customer;
import com.mmi.tauProjekt.QrCode.CustomerPaymentToken;
import com.mmi.tauProjekt.QrCode.QrCodeGenerator;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static com.mmi.tauProjekt.Security.SecurityConstants.SECRET;
import static com.mmi.tauProjekt.Security.SecurityConstants.TOKEN_PREFIX;



@RestController
@RequestMapping("/customers")
public class CustomerController {
    @Autowired
    private CustomerList list;
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private PriceList plist = new PriceList();
    private CustomerPaymentToken customerPaymentToken;

    public CustomerController(CustomerList list,
                             BCryptPasswordEncoder bCryptPasswordEncoder, CustomerPaymentToken customerPaymentToken) {
        this.list = list;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.customerPaymentToken = customerPaymentToken;
    }





    //Ogrenci kayit yeri
    //Token gerektirmez
    @RequestMapping(value = "/sign-up", method = RequestMethod.POST)
    public void signUp(@RequestBody Customer s) throws CustomException{
    if (list.getCustomer(s.getId()) != null){
        throw new CustomException("CustomerAlreadyExist");
    }

        s.setPassword(bCryptPasswordEncoder.encode(s.getPassword()));
        list.addCustomer(s);
    }




    //Ogrenci bilgisini geitrmek için method
    //Gonderilen json dosyası sadece jwt token içermeli
    //Ogrenci sinfini sifresi silinmis bir sekilde geri dondurur
    @RequestMapping(value = "/get-info",method = RequestMethod.POST)
    public Customer getInfo( @RequestHeader("Authorization") String token){
        String customerId = tokenToCustomerIdParser(token);
        Customer s = list.getCustomerWithoutPass(customerId);
        if (s == null) {
            throw new UsernameNotFoundException(customerId);
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
        String customerId = tokenToCustomerIdParser(token);
        if (list.getCustomer(customerId).getBalance()>priceAmount){
            list.getCustomer(customerId).setBalance( list.getCustomer(customerId).getBalance() - priceAmount);
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
        String customerId = tokenToCustomerIdParser(token);

        Customer r = list.getCustomer(customerId);
        if (r == null) {
            throw new UsernameNotFoundException(customerId);
        }

        r.setBalance( r.getBalance() + amount);
        return ("deposited successfully");
    }






    //Para yollama methodu
    //gonderilen, ne kadar gonderildigi ve jwt tokeni json içerisine yazılmalı
    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    public String transfer(@RequestBody moneyTransferInfo mti, @RequestHeader("Authorization") String token) throws CustomException {
        String sender = tokenToCustomerIdParser(token);
        String receiver = mti.getReceiverId();
        int amount = mti.getAmount();

        Customer r = list.getCustomer(receiver);
        if (r == null) {
            throw new UsernameNotFoundException(receiver);
        }

        if (list.getCustomer(sender).getBalance() > amount){
            list.getCustomer(sender).setBalance( list.getCustomer(sender).getBalance() - amount);
            list.getCustomer(receiver).setBalance(list.getCustomer(receiver).getBalance() + amount);
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
        String customerId = tokenToCustomerIdParser(token);

        Customer s = list.getCustomer(customerId);
        s.setPassword(newPass);
        return "password changed successfully";
    }





    //Odeme QrCode uretmek icin method
    //Aybuke uuid generator ile birlestirildi
    //S
    //
    @RequestMapping(value = "/request-qr-code", method = RequestMethod.POST)
    private String getQrCode(@RequestHeader("Authorization") String token) {
        String customerId = tokenToCustomerIdParser(token);
        String qrCode = customerPaymentToken.getPaymentToken(customerId);

        return qrCode;
    }





    //Barkod okucudan gelen json dosyasini okuyup isleme sokar
    //Barkod okuyucu okudgu qr kod icerigini ve urun idsini yollamali
    //
    @RequestMapping(value = "/send-qr-code", method = RequestMethod.POST)
    private String sendQrCode(@RequestBody QrCodeJsonParser qrCodeJsonParser){
        String qrCode =qrCodeJsonParser.getQrCode();
        String priceId = qrCodeJsonParser.getPriceId();
        String customerId = customerPaymentToken.confirmPaymentToken(qrCode);

        if (customerId!=null){

            int priceAmount = plist.getPrice(priceId);
            if (priceAmount == -1){
                return "price not found";
            }
            if (list.getCustomer(customerId).getBalance()>priceAmount){
                list.getCustomer(customerId).setBalance( list.getCustomer(customerId).getBalance() - priceAmount);
                return "paid successfully";
            }else {
                return "insufficient balance";

            }
        }else {
            return "qr code not found";
        }
    }





    //token icindeki kullanici adini geri dondurur
    private String tokenToCustomerIdParser(String token){
        String Customer = Jwts.parser()
                .setSigningKey(SECRET.getBytes())
                .parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
                .getBody()
                .getSubject();
        return Customer;
    }
/*
    @Bean
    public CustomerList list(){
        return list;
    }
    */
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
    String priceId;

    public QrCodeJsonParser(){

    }

    public QrCodeJsonParser(String qrCode){
        this.qrCode = qrCode;
    }

    public String getQrCode() {
        return qrCode;
    }

    public String getPriceId(){
        return priceId;
    }


}
