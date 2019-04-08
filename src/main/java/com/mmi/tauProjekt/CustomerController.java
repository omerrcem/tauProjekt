package com.mmi.tauProjekt;

import com.mmi.tauProjekt.Entity.Customer;
import com.mmi.tauProjekt.Lists.FeedbackList;
import com.mmi.tauProjekt.Lists.PriceList;
import com.mmi.tauProjekt.Lists.RecommendList;
import com.mmi.tauProjekt.Lists.CustomerList;
import com.mmi.tauProjekt.Mail.MailService;
import com.mmi.tauProjekt.QrCode.CustomerPaymentToken;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.Random;

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
    private MailService mailService;
    private RecommendList listRecommend;
    private FeedbackList feedbackList;


    public CustomerController(CustomerList list,
                             BCryptPasswordEncoder bCryptPasswordEncoder,
                              CustomerPaymentToken customerPaymentToken,
                              MailService mailService,
                              RecommendList listRecommend,
                              FeedbackList feedbackList) {
        this.list = list;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.customerPaymentToken = customerPaymentToken;
        this.mailService = mailService;
        this.listRecommend = listRecommend;
        this.feedbackList=feedbackList;
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




    //Kullanici adini ister ve gizlenmis br sekilde gonderir
    //
    @RequestMapping(value = "/get-name",method = RequestMethod.POST)
    public String getName( @RequestHeader("Authorization") String token, @RequestBody IdInfo idInfo){

        Customer s = list.getCustomer(idInfo.getId());

        if (s==null){
            throw  new UsernameNotFoundException(idInfo.getId());
        }


        String name = s.getName();
        char[] nameArray = name.toCharArray();

        for (int i = 2; i <nameArray.length ; i++) {

            if (nameArray[i] == ' '){
                i= i+2;
                continue;
            }else {
                nameArray[i] = '*';
            }

        }
        return new String(nameArray);
    }




    //Test Odeme methodu normalde odeme islemi qr kod uzerinden oluyor
    //gonderen kisi bir json dosyasına bir jwt token, nereye odedigini
    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public String pay(@RequestBody PayType pt, @RequestHeader("Authorization") String token) throws CustomException {

        String priceId = pt.getPriceId();
        String customerId = tokenToCustomerIdParser(token);
        Customer customer = list.getCustomer(customerId);

        double priceAmount = plist.getPrice(customer.getStatus(),pt.getPriceId());

        switch (priceId) {

            case "mensa":

                if (customer.getBalanceMensa()>priceAmount){
                    customer.setBalanceMensa( customer.getBalanceMensa() - priceAmount);
                    return "paid successfully";
                }else {
                    return "insufficient balance";
                }

            case "shuttle":

                if (customer.getBalanceShuttle()>priceAmount){
                    customer.setBalanceShuttle( customer.getBalanceShuttle() - priceAmount);
                    return "paid successfully";
                }else {
                    return "insufficient balance";
                }


            default:
                return "price not found";

        }




    }




    //Para yukleme methodu
    //gonderen kisi bir json dosyasinda yuklenecek miktari ve tokeni gondermeli
    @RequestMapping(value = "/deposit", method = RequestMethod.POST)
    public String deopsit(@RequestHeader("Authorization") String token, @RequestBody DepositInfo depositInfo){
        String balanceId = depositInfo.getBalanceId();
        int amount = depositInfo.getAmount();
        String customerId = tokenToCustomerIdParser(token);

        Customer r = list.getCustomer(customerId);
        if (r == null) {
            throw new UsernameNotFoundException(customerId);
        }

        switch (balanceId){

            case"mensa":
                r.setBalanceMensa( r.getBalanceMensa() + amount);
                return ("deposited successfully");


            case "shuttle":
                r.setBalanceShuttle( r.getBalanceShuttle() + amount);
                return ("deposited successfully");


            default:
                return ("balance not found");

        }

    }





    //Para yollama methodu
    //gonderilen, ne kadar gonderildigi ve jwt tokeni json içerisine yazılmalı
    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    public String transfer(@RequestBody moneyTransferInfo mti, @RequestHeader("Authorization") String token) throws CustomException {
        String sender = tokenToCustomerIdParser(token);
        String receiver = mti.getReceiverId();
        String balanceId = mti.getBalanceId();
        int amount = mti.getAmount();

        Customer r = list.getCustomer(receiver);
        if (r == null) {
            throw new UsernameNotFoundException(receiver);
        }

        switch (balanceId) {

            case "mensa":

                if (list.getCustomer(sender).getBalanceMensa() > amount){
                    list.getCustomer(sender).setBalanceMensa( list.getCustomer(sender).getBalanceMensa() - amount);
                    list.getCustomer(receiver).setBalanceMensa(list.getCustomer(receiver).getBalanceMensa() + amount);
                    return "transfered successfully";
                }else {
                    return "insufficient balance";

                }

            case "shuttle":

                if (list.getCustomer(sender).getBalanceShuttle() > amount){
                    list.getCustomer(sender).setBalanceShuttle( list.getCustomer(sender).getBalanceShuttle() - amount);
                    list.getCustomer(receiver).setBalanceShuttle(list.getCustomer(receiver).getBalanceShuttle() + amount);
                    return "transfered successfully";
                }else {
                    return "insufficient balance";

                }

            default:
                return "balance not found";

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
    @RequestMapping(value = "/request-qr-code", method = RequestMethod.POST)
    private String getQrCode(@RequestHeader("Authorization") String token) {
        String customerId = tokenToCustomerIdParser(token);
        String qrCode = customerPaymentToken.getPaymentToken(customerId);
        System.out.println(qrCode+ " für "+ customerId);
        return qrCode;
    }



    //Barkod okucudan gelen json dosyasini okuyup isleme sokar
    //Barkod okuyucu okudgu qr kod icerigini ve urun idsini yollamali
    //
    @RequestMapping(value = "/send-qr-code", method = RequestMethod.POST)
    private String sendQrCode(@RequestBody QrCodeJsonParser qrCodeJsonParser){
        String qrCode =qrCodeJsonParser.getQrCode();
        String priceId = qrCodeJsonParser.getPriceId();
        String customerId = customerPaymentToken.getCustomerId(qrCode);
        Customer customer = list.getCustomer(customerId);

        if (customerId!=null){

            double priceAmount = plist.getPrice(customer.getStatus(),priceId);

            String answer;

            switch (priceId) {

                case "mensa":

                            if (customer.getBalanceMensa()>priceAmount){
                                customer.setBalanceMensa( customer.getBalanceMensa() - priceAmount);
                                answer="paid successfully";
                                break;
                            }else {
                                answer = "insufficient balance";
                                break;
                            }

                case "shuttle":

                            if (customer.getBalanceShuttle()>priceAmount){
                                customer.setBalanceShuttle( customer.getBalanceShuttle() - priceAmount);
                                answer = "paid successfully";
                                break;
                            }else {
                                answer = "insufficient balance";
                                break;
                            }


                default:
                            answer= "price not found";
                            break;


            }
            customerPaymentToken.confirmPaymentToken(qrCode,answer);
            return answer;

        }else {
            return "qr code not found";
        }
    }




    @RequestMapping(value = "/is-paid", method = RequestMethod.POST)
    public String isQrPaid(@RequestBody IsPaidInfo isPaidInfo,  @RequestHeader("Authorization") String token){

        String qrCode = isPaidInfo.getQrCode();

       return customerPaymentToken.isPaid(qrCode);
    }




    @RequestMapping(value = "/feedback", method = RequestMethod.POST)
    public String feedback(@RequestBody FeedbackInfo feedbackInfo, @RequestHeader("Authorization") String token){

        String customerId = tokenToCustomerIdParser(token);
        int star = feedbackInfo.getStar();
        String text = feedbackInfo.getText();

        //TODO: Aybuke sinif yazip entegre edecek
        feedbackList.addFeedback(customerId,star,text);


        return "feedback successfully sent";
    }




    //TODO: Recommend sinifi entegre edilecek
    //Kullanici bir kisi onermek icin bu fonksiyonu cagirir
    //Json icinde bir id olması ve yaninda token gonderilmesi gerekir
    @RequestMapping(value = "/recommend", method = RequestMethod.POST)
    public String recommend(@RequestBody IdInfo idInfo, @RequestHeader("Authorization") String token){

        String customerId = tokenToCustomerIdParser(token);
        Customer c = list.getCustomer(customerId);

        if (c == null){
            throw new UsernameNotFoundException(customerId);
        }

        //TODO: yazilacak sinifi empfehlen ekleme methodu
        listRecommend.addRecommend(customerId);

        return "recommended successfully";
    }




    //Sifre unuttum kismi
    //Kullanici token olmadan bir musteri IDsi yollar, eger boyle bir kullanici var ise mail gonderir
    //ve donus olarak hangi maile gonderdigini String olarak yollar
    @RequestMapping(value = "/forgot-password",method = RequestMethod.POST)
    private String forgotPassword(@RequestBody IdInfo idInfo) throws MessagingException {

        Customer customer = list.getCustomer(idInfo.getId());
        if (customer == null){
            throw new UsernameNotFoundException(idInfo.getId());
        }
        String mail = customer.getMail();

        String newPass = getSaltString();

        //Burada maili atar
        mailService.sendEmail("support@tau-pay.com",mail,"Password Recovery",
                            "Hi "+customer.getName()+",<br><br>"
                                    +"Your new generated password is: "+"<b> "+newPass+" </b><br>"
                                    +"This mail has been sent automatically, please do not reply.<br>"
                                    +"Tau-Pay Support");

        customer.setPassword(bCryptPasswordEncoder.encode(newPass));


        char[] nameArray = mail.toCharArray();

        for (int i = 3; i <nameArray.length ; i++) {

           if (nameArray[i] == '@'){
               break;
           }
           nameArray[i]='*';
        }


        return new String(nameArray);
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





    //Random sifre uretir
    protected String getSaltString() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < 7) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        String saltStr = salt.toString();
        return saltStr;

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
    String balanceId;
    int amount;

    public String getReceiverId() {
        return receiverId;
    }
    public String getBalanceId(){return balanceId;}
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
class DepositInfo{
    String balanceId;
    int amount;

    public String getBalanceId(){return balanceId;}
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

//Odendi mi sorgusu icin json sinifi
class IsPaidInfo{
    String qrCode;

    public String getQrCode() {
        return qrCode;
    }
}


//Id bilgisini iceren sinif json icindir
class IdInfo{
    String id;

    public String getId() {
        return id;
    }
}

//Feedback bilgisini tasiyan sinif
class FeedbackInfo{
    int star;
    String text;

    public int getStar() {
        return star;
    }

    public String getText() {
        return text;
    }
}

