package com.mmi.tauProjekt.ForgotPassword;


import com.mmi.tauProjekt.Entity.Customer;
import com.mmi.tauProjekt.Entity.ForgotPasswordToken;
import com.mmi.tauProjekt.Repository.CustomerRepository;
import com.mmi.tauProjekt.Repository.ForgotPasswordTokenRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Calendar;
import java.util.Date;


//Mailde gelen linke tiklayinca bu sinif calisir
@Controller
public class ForgotPasswordController {

    private CustomerRepository customerRepository;
    private ForgotPasswordTokenRepository forgotPasswordTokenRepository;

    public ForgotPasswordController(CustomerRepository customerRepository,
                                    ForgotPasswordTokenRepository forgotPasswordTokenRepository) {
        this.customerRepository = customerRepository;
        this.forgotPasswordTokenRepository=forgotPasswordTokenRepository;
    }

    //Mailde gelen linki icin sifremi unuttum html sayfasini gonderir
    //Tiklanan link kisiye ve isleme ozeldir, sifre degistikten sonra expire olur
    @RequestMapping(value = "/forgot-password",method = RequestMethod.GET)
    public String newPassHtml(@RequestParam String passwordToken){

        ForgotPasswordToken forgotPasswordToken = forgotPasswordTokenRepository.findById(passwordToken)
                                                    .orElse(new ForgotPasswordToken());

        if (forgotPasswordToken.getToken() != null){
            //Link gecerli ise sifre degistirme sayfasi doner
            return "forgot-password.html";
        }else {
            //Link gecersis ise 404 sayfasi doner
            return new ResponseEntity(HttpStatus.NOT_FOUND).toString();
        }
    }
}



//Sifre degistirme sayfasindan bilgileri alan sinif
@RestController
class ForgotPasswordRestController{

    private CustomerRepository customerRepository;
    private ForgotPasswordTokenRepository forgotPasswordTokenRepository;

    public ForgotPasswordRestController(CustomerRepository customerRepository,
                                        ForgotPasswordTokenRepository forgotPasswordTokenRepository) {
        this.customerRepository = customerRepository;
        this.forgotPasswordTokenRepository = forgotPasswordTokenRepository;
    }


    //Html sayfasindaki sifreyi degistire tiklayinca bu fonksiyon calisir
    //
    @RequestMapping(value = "/confirm-forgot-password",method =RequestMethod.POST)
    public String confirmForgotPass(@RequestBody NewPassInfo newPassInfo){
        String urlToken = newPassInfo.getUrl().split("=")[1];
        String pass = newPassInfo.getPassword();
        ForgotPasswordToken forgotPasswordToken = forgotPasswordTokenRepository.findById(urlToken).orElse(new ForgotPasswordToken());

        String passwordToken = forgotPasswordToken.getToken();


        if (passwordToken != null){
            Date createDate = forgotPasswordToken.getCreateDate();
            Date expireDate = getExpireDate(createDate);

            if (expireDate.compareTo(createDate) < 0){
                return "error with parsing token";
            }

            Customer customer =customerRepository.findById(forgotPasswordToken.getCustomerId()).orElse(new Customer());

            if (customer.getId()==null){
                return "customer not found";
            }

            customer.setPassword(pass);
            customerRepository.save(customer);
            forgotPasswordTokenRepository.delete(forgotPasswordToken);
            return "password changed successfully";
        }else {

            return "error with parsing token";
        }


    }



    public Date getExpireDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR_OF_DAY, 24);
        return calendar.getTime();
    }


}



class NewPassInfo{
    public String password;
    public String url;

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }
}