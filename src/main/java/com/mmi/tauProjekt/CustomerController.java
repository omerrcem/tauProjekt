package com.mmi.tauProjekt;

import com.mmi.tauProjekt.Entity.*;
import com.mmi.tauProjekt.Mail.MailService;
import com.mmi.tauProjekt.QrCode.CustomerPaymentToken;
import com.mmi.tauProjekt.Repository.*;
import io.jsonwebtoken.Jwts;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.*;

import static com.mmi.tauProjekt.Security.SecurityConstants.SECRET;
import static com.mmi.tauProjekt.Security.SecurityConstants.TOKEN_PREFIX;
import static com.mmi.tauProjekt.ServerInfo.ServerInfo.mainUrl;


@RestController
@RequestMapping("/customers")
@Api(value="customers", description="Para işlemleri, müşteri bilgileri vb. işlemler burada yürütülür.")
public class CustomerController {

    @Autowired

    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private CustomerPaymentToken customerPaymentToken;
    private MailService mailService;
    private CustomerRepository customerRepository;
    private FeedbackRepository feedbackRepository;
    private PriceRepository priceRepository;
    private RecommendRepository recommendRepository;
    private TransactionRepository transactionRepository;
    private PoolAccountRepository poolAccountRepository;
    private ForgotPasswordTokenRepository forgotPasswordTokenRepository;

    public CustomerController(BCryptPasswordEncoder bCryptPasswordEncoder,
                              CustomerPaymentToken customerPaymentToken,
                              MailService mailService,
                              CustomerRepository customerRepository,
                              FeedbackRepository feedbackRepository,
                              PriceRepository priceRepository,
                              RecommendRepository recommendRepository,
                              TransactionRepository transactionRepository,
                              PoolAccountRepository poolAccountRepository,
                              ForgotPasswordTokenRepository forgotPasswordTokenRepository) {

        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.customerPaymentToken = customerPaymentToken;
        this.mailService = mailService;
        this.customerRepository=customerRepository;
        this.feedbackRepository=feedbackRepository;
        this.priceRepository=priceRepository;
        this.recommendRepository=recommendRepository;
        this.transactionRepository=transactionRepository;
        this.poolAccountRepository = poolAccountRepository;
        this.forgotPasswordTokenRepository = forgotPasswordTokenRepository;

    }



    //Ogrenci kayit yeri
    //Token gerektirmez
            @ApiOperation(value = "Kayıt olma fonksiyonu (Kullanım dışı)")
            @ApiResponses(value = {
                    @ApiResponse(code = 200, message =  "(user added) = kullanıcı başarıyla eklendi\n " +
                                                        "(user already exist) = kullanıcı zaten mevcut")
            })
    @RequestMapping(value = "/sign-up", method = RequestMethod.POST)
    public String signUp(@RequestBody Customer customer) throws CustomException{
        Customer found = customerRepository.findById(customer.getId()).orElse(new Customer());

    if (found.getId() != null){
        return"user already exist";
    }

        customerRepository.save(customer);
        return "user added";
    }



    //Ogrenci bilgisini geitrmek için method
    //Gonderilen json dosyası sadece jwt token içermeli
    //Ogrenci sinfini sifresi silinmis bir sekilde geri dondurur
            @ApiOperation(value = "Kullanıcı bilgisini getirir")
            @ApiResponses(value = {
                    @ApiResponse(code = 200, message = "Kullanıcı bilgisi"),
                    @ApiResponse(code = 403, message = "Hatalı token bilgisi")
            })
    @RequestMapping(value = "/get-info",method = RequestMethod.POST)
    public Customer getInfo( @RequestHeader("Authorization") String token){
        String customerId = tokenToCustomerIdParser(token);
        //Customer s = list.getCustomerWithoutPass(customerId);
        Customer found = customerRepository.findById(customerId).orElse(new Customer());

        if (found.getId() == null) {
            throw new UsernameNotFoundException(customerId);
        }else {
            Customer s = new Customer();
            s.setId(found.getId());
            s.setName(found.getName());
            s.setMail(found.getMail());
            s.setStatus(found.getStatus());
            s.setBalanceMensa(found.getBalanceMensa());
            s.setBalanceShuttle(found.getBalanceShuttle());
            s.setCanGetFreeItem(found.isCanGetFreeItem());

            return s;
        }
    }



    //Kullanici adini ister ve gizlenmis br sekilde gonderir
    //
            @ApiOperation(value = "Transfer işleminden önce kime para atılacağını gösteren fonksiyon")
            @ApiResponses(value = {
                    @ApiResponse(code = 200, message = "Kişinin gizlenmiş ismi"),
                    @ApiResponse(code = 403, message = "Hatalı token bilgisi")
            })
    @RequestMapping(value = "/get-name",method = RequestMethod.POST)
    public String getName( @RequestHeader("Authorization") String token, @RequestBody IdInfo idInfo){

        String customerId = idInfo.getId();
        Customer s = customerRepository.findById(customerId).orElse(new Customer());

        if (s.getId()==null){
            return "user not found";
        }


        String name = s.getName();
        char[] nameArray = name.toCharArray();

        for (int i = 2; i <nameArray.length ; i++) {

            if (nameArray[i] == ' ' || nameArray[i] == '.'){
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
            @ApiOperation(value = "Ödeme işlemi için fonksiyon (Kullanım dışı)")
            @ApiResponses(value = {
                    @ApiResponse(code = 200, message = "(error with pricing) = priceId değişkeni hatalı\n"+
                                                        "(insufficient balance) = yetersiz bakiye\n"+
                                                        "(paid successfully) = ödeme başarıyla tamamlandı"),
                    @ApiResponse(code = 403, message = "Hatalı token bilgisi")
            })
    @RequestMapping(value = "/pay", method = RequestMethod.POST)
    public String pay(@RequestBody PayType pt, @RequestHeader("Authorization") String token) throws CustomException {

        String priceId = pt.getPriceId();
        String customerId = tokenToCustomerIdParser(token);
        Customer customer = customerRepository.findById(customerId).orElse(new Customer());


        double priceAmount = getPrice(priceId,customer.getStatus());
        if (priceAmount==-1){
            return "error with pricing";
        }
        String answer;

                if (checkFreeItem(customerId,priceId)){

                    answer = "received free item";
                    PoolAccount poolAccount = poolAccountRepository.findById(priceId).orElse(new PoolAccount());
                    poolAccount.setBalance(poolAccount.getBalance()-priceAmount);
                    poolAccountRepository.save(poolAccount);

                }else {
                    switch (priceId) {


                        case "mensa":

                            if (customer.getBalanceMensa() > priceAmount) {
                                customer.setBalanceMensa(customer.getBalanceMensa() - priceAmount);
                                answer = "paid successfully";
                                break;
                            } else {
                                answer = "insufficient balance";
                                break;
                            }

                        case "shuttle":

                            if (customer.getBalanceShuttle() > priceAmount) {
                                customer.setBalanceShuttle(customer.getBalanceShuttle() - priceAmount);
                                answer = "paid successfully";
                                break;
                            } else {
                                answer = "insufficient balance";
                                break;
                            }


                        default:
                            answer = "price not found";
                            break;
                    }
                }
        customerRepository.save(customer);
        logTransaction(customerId,"Pay",answer,priceId,priceAmount  );
        return answer;


    }



    //Para yukleme methodu
    //gonderen kisi bir json dosyasinda yuklenecek miktari ve tokeni gondermeli
            @ApiOperation(value = "Para yükleme fonksiyonu")
            @ApiResponses(value = {
                    @ApiResponse(code = 200, message = "(balance not found) = bakiye bilgisi hatalı\n"+
                                                       "(deposited successfully) = para başaryla yüklendi\n"),

                    @ApiResponse(code = 403, message = "Hatalı token bilgisi")
            })
    @RequestMapping(value = "/deposit", method = RequestMethod.POST)
    public String deopsit(@RequestHeader("Authorization") String token, @RequestBody DepositInfo depositInfo){
        String balanceId = depositInfo.getBalanceId();
        int amount = depositInfo.getAmount();
        String customerId = tokenToCustomerIdParser(token);

        Customer r = customerRepository.findById(customerId).orElse(new Customer());
        if (r.getId() == null) {
            throw new UsernameNotFoundException(customerId);
        }
        String answer;

        switch (balanceId){

            case"mensa":
                r.setBalanceMensa( r.getBalanceMensa() + amount);
                answer= ("deposited successfully");
                break;

            case "shuttle":
                r.setBalanceShuttle( r.getBalanceShuttle() + amount);
                answer=  ("deposited successfully");
                break;

            default:
                answer= ("balance not found");
                break;
        }

        customerRepository.save(r);
        logTransaction(customerId,"Deposit",answer,balanceId,amount);
        return answer;

    }



    //Para yollama methodu
    //gonderilen, ne kadar gonderildigi ve jwt tokeni json içerisine yazılmalı
            @ApiOperation(value = "Para transferi için fonksiyon")
            @ApiResponses(value = {
                    @ApiResponse(code = 200, message = "(balance not found) = bakiye bilgisi yanlış\n"+
                                                       "(user not found) = kullanıcı bulunamadı\n"+
                                                       "(insufficient balance) = yetersiz bakiye\n"+
                                                       "(transfered successfully) = para transferi başarıyla tamamlandı"),
                    @ApiResponse(code = 403, message = "Hatalı token bilgisi")
            })
    @RequestMapping(value = "/transfer", method = RequestMethod.POST)
    public String transfer(@RequestBody moneyTransferInfo mti, @RequestHeader("Authorization") String token) throws CustomException, InterruptedException {
        String sender = tokenToCustomerIdParser(token);
        String receiver = mti.getReceiverId();
        String balanceId = mti.getBalanceId();
        int amount = mti.getAmount();

        if(amount<0){
            return "error";
        }

        Customer s = customerRepository.findById(sender).orElse(new Customer());
        Customer r = customerRepository.findById(receiver).orElse(new Customer());
        if (r.getId() == null) {
            return "user not found";
        }
        String answer;
        switch (balanceId) {

            case "mensa":

                if (s.getBalanceMensa() > amount){
                    s.setBalanceMensa( s.getBalanceMensa() - amount);
                    r.setBalanceMensa(r.getBalanceMensa() + amount);
                    answer= "transfered successfully";
                    break;
                }else {
                    answer= "insufficient balance";
                    break;
                }

            case "shuttle":

                if (s.getBalanceShuttle() > amount){
                    s.setBalanceShuttle( s.getBalanceShuttle() - amount);
                    r.setBalanceShuttle(r.getBalanceShuttle() + amount);
                    answer= "transfered successfully";
                    break;
                }else {
                    answer= "insufficient balance";
                    break;
                }

            default:
                answer= "balance not found";
                break;

        }
        customerRepository.save(s);
        customerRepository.save(r);

        logTransaction( sender,"Send",answer,balanceId,amount);

        if (answer.equals("transfered successfully")){


            logTransaction(receiver,"Receive","received successfully",balanceId,amount);
        }
        return answer;
    }



    //Sifre degistirme methodu
    //Gonderen kisinin json dosyasi icinde jwt token ve yeni sifreyi gondermesi gerekir
            @ApiOperation(value = "Profil ayarlarından şifre değiştirmek için fonksiyon")
            @ApiResponses(value = {
                    @ApiResponse(code = 200, message = "(wrong password) = eski şifre hatalı\n"+
                                                       "(password changed successfully) = şifre başarıyla değiştirildi\n"),
                    @ApiResponse(code = 403, message = "Hatalı token bilgisi")
            })
    @RequestMapping(value = "/change-password", method = RequestMethod.POST)
    private String changePassword(@RequestBody PasswordInfo passInfo, @RequestHeader("Authorization") String token){
        String newPass = passInfo.getNewPass();
        String oldPass = passInfo.getOldPass();
        String customerId = tokenToCustomerIdParser(token);

        Customer s = customerRepository.findById(customerId).orElse(new Customer());

        if(!s.getPassword().equals(oldPass)){
            return "wrong password";
        }

        s.setPassword(newPass);
        customerRepository.save(s);
        return "password changed successfully";
    }



    //Odeme QrCode uretmek icin method
    //Aybuke uuid generator ile birlestirildi
    //S
            @ApiOperation(value = "Qr kod istemek için fonksiyon")
            @ApiResponses(value = {
                    @ApiResponse(code = 200, message = "gelen String qr kodun kendisidir"),
                    @ApiResponse(code = 403, message = "Hatalı token bilgisi")
            })
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
            @ApiOperation(value = "Qr kod tamının fonksiyonu, okudukları qr kodu bu adrese yollayacaklar")
            @ApiResponses(value = {
                    @ApiResponse(code = 200, message =  "(error with pricing) = priceId değişkeni hatalı\n"+
                                                        "(insufficient balance) = yetersiz bakiye\n"+
                                                        "(paid successfully) = ödeme başarıyla tamamlandı\n"+
                                                        "(qr code not found) = Qr kod geçerli değil")
            })
    @RequestMapping(value = "/send-qr-code", method = RequestMethod.POST)
    private String sendQrCode(@RequestBody QrCodeJsonParser qrCodeJsonParser){
        String qrCode =qrCodeJsonParser.getQrCode();
        String priceId = qrCodeJsonParser.getPriceId();
        String customerId = customerPaymentToken.getCustomerId(qrCode);
        Customer customer = customerRepository.findById(customerId).orElse(new Customer());

        if (customerId!=null){

            double priceAmount = getPrice(priceId,customer.getStatus());
            if (priceAmount==-1){
                return "error with pricing";
            }

            String answer;

            //Ucretsiz hizmet alabiliyor mu diye kontrol eder
            if (checkFreeItem(customerId,priceId)){

                answer = "received free item";
                PoolAccount poolAccount = poolAccountRepository.findById(priceId).orElse(new PoolAccount());
                poolAccount.setBalance(poolAccount.getBalance()-priceAmount);
                poolAccountRepository.save(poolAccount);

            }else {


                switch (priceId) {

                    case "mensa":

                        if (customer.getBalanceMensa() > priceAmount) {
                            customer.setBalanceMensa(customer.getBalanceMensa() - priceAmount);
                            answer = "paid successfully";
                            break;
                        } else {
                            answer = "insufficient balance";
                            break;
                        }

                    case "shuttle":

                        if (customer.getBalanceShuttle() > priceAmount) {
                            customer.setBalanceShuttle(customer.getBalanceShuttle() - priceAmount);
                            answer = "paid successfully";
                            break;
                        } else {
                            answer = "insufficient balance";
                            break;
                        }


                    default:
                        answer = "price not found";
                        break;


                }
            }
            customerRepository.save(customer);
            customerPaymentToken.confirmPaymentToken(qrCode,answer);

            logTransaction(customerId,"Pay",answer,priceId,priceAmount);

            return answer;

        }else {
            return "qr code not found";
        }
    }



    //Client bir qr kod request ettikten sonra sürekli bu fonksiyonu cagirir
            @ApiOperation(value = "Mobil uygulamadan ödendi mi sorgusu için fonksiyon")
            @ApiResponses(value = {
                    @ApiResponse(code = 200, message = "(not paid) = henüz ödenmedi\n"+
                                                       "(qr code not found) = Qr kod bulunamdı\n"+
                                                       "(insufficient balance) = yetersiz bakiye\n"+
                                                       "(paid successfully) = ödeme başarıyla tamamlandı"),
                    @ApiResponse(code = 403, message = "Hatalı token bilgisi")
            })
    @RequestMapping(value = "/is-paid", method = RequestMethod.POST)
    public String isQrPaid(@RequestBody IsPaidInfo isPaidInfo,  @RequestHeader("Authorization") String token){

        String qrCode = isPaidInfo.getQrCode();

       return customerPaymentToken.isPaid(qrCode);
    }



    //Feedback alan fonksiyon
    //Json icinde yildiz sayisi, mensa veya shuttle mi oldugu ve feedback texti gonderilmeli
            @ApiOperation(value = "Feedback eklemek için fonksiyon")
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "(feedback successfully sent) = feedback başarıyla eklendi"),
            @ApiResponse(code = 403, message = "Hatalı token bilgisi")
    })
    @RequestMapping(value = "/feedback", method = RequestMethod.POST)
    public String feedback(@RequestBody FeedbackInfo feedbackInfo, @RequestHeader("Authorization") String token){

        String customerId = tokenToCustomerIdParser(token);
        int star = feedbackInfo.getStar();
        String text = feedbackInfo.getText();
        String type = feedbackInfo.getType();

        //TODO: Aybuke sinif yazip entegre edecek
        com.mmi.tauProjekt.Entity.FeedbackInfo infos= new com.mmi.tauProjekt.Entity.FeedbackInfo(customerId,star,type,text);
        feedbackRepository.save(infos);

        return "feedback successfully sent";
    }



    //Kullanici bir kisi onermek icin bu fonksiyonu cagirir
    //Json icinde bir id olması ve yaninda token gonderilmesi gerekir
            @ApiOperation(value = "Bir kişinin askıda ürün alabilmesi için önerildiği fonksiyon")
            @ApiResponses(value = {
                    @ApiResponse(code = 200, message = "(user not found) = böyle bir kullanıcı yok\n"+
                                                       "(recommended successfully) = başarıyla önerildi\n"),
                    @ApiResponse(code = 403, message = "Hatalı token bilgisi")
            })
    @RequestMapping(value = "/recommend", method = RequestMethod.POST)
    public String recommend(@RequestBody IdInfo idInfo, @RequestHeader("Authorization") String token){

        String customerId = tokenToCustomerIdParser(token);
        Customer c = customerRepository.findById(customerId).orElse(new Customer());
        Recommend r = recommendRepository.findById(customerId).orElse(new Recommend());

        if (c.getId() == null){
            return "user not found";
        }

        if (r.getCustomerId() == null) {

            Recommend recommend = new Recommend(idInfo.getId());
            r=recommend;
        }else {
            r.increaseRecommendTime();
        }
        recommendRepository.save(r);

        return "recommended successfully";
    }


    //Sifre unuttum kismi
    //Kullanici token olmadan bir musteri IDsi yollar, eger boyle bir kullanici var ise mail gonderir
    //ve donus olarak hangi maile gonderdigini String olarak yollar
            @ApiOperation(value = "Şifremi unuttum fonksiyonu")
            @ApiResponses(value = {
                    @ApiResponse(code = 200, message = "(user not found) = böyle bir kişi yok\n"+
                                                       "bir mail Stringi = gerekli mail verilen adrese gönderildi\n"),
                    @ApiResponse(code = 403, message = "Hatalı token bilgisi")
            })
    @RequestMapping(value = "/forgot-password",method = RequestMethod.POST)
    private String forgotPassword(@RequestBody IdInfo idInfo) throws MessagingException {

        Customer customer = customerRepository.findById(idInfo.getId()).orElse(new Customer());

        if (customer.getId() == null){
            return "user not found";
        }
        String mail = customer.getMail();
        String customerId = customer.getId();
        UUID randomUUID = UUID.randomUUID();
        String token = randomUUID.toString();

        String passwordResetUrl = mainUrl+"/forgot-password?passwordToken="+token;


        forgotPasswordTokenRepository.save(new ForgotPasswordToken(token,customerId));

        mailService.sendEmail("support@tau-pay.com",mail,"Şifre Sıfırlama",
                "Merhaba "+customer.getName()+",<br><br>"
                        +"Şifrenizi sıfırlamak için aşağıdaki linke tıklayınız:<br>"+"<a href="+passwordResetUrl+"><b> "+passwordResetUrl+" </b></a><br><br>"
                        +"Şifre sıfırlama isteğini siz göndermedeiyseniz bu e-postayı görmezden geliniz.<br>"
                        +"Verilen link sadece 24 saat geçerlidir.<br>"
                        +"Bu e-posta otomatik olarak gönderilmiştir, lütfen cevaplamayınız.<br>"
                        +"Tau-Pay Destek");


        char[] nameArray = mail.toCharArray();
        for (int i = 3; i <nameArray.length ; i++) {

           if (nameArray[i] == '@'){
               break;
           }
           nameArray[i]='*';
        }


        return new String(nameArray);
    }




    //Bagis yapmak icin kullanilan fonksiyon
    //json icinde hangi hesap  icin gonderildigi ve kac kere gonderildigi saklanir
            @ApiOperation(value = "Bağış fonksiyonu")
            @ApiResponses(value = {
                    @ApiResponse(code = 200, message =  "(price not found) = priceId değişkeni hatalı\n"+
                                                        "(insufficient balance) = yetersiz bakiye\n"+
                                                        "(donated successfully) = bağış işlemi başarıyla tamamlandı"),
                    @ApiResponse(code = 403, message = "Hatalı token bilgisi")
            })
    @RequestMapping(value = "/donate-item", method = RequestMethod.POST)
    private String donateItem(@RequestBody FreeItemInfo freeItemInfo, @RequestHeader("Authorization")String token){
        String customerId = tokenToCustomerIdParser(token);
        Customer c = customerRepository.findById(customerId).orElse(new Customer());;
        double price= getPrice(freeItemInfo.priceId,"Student");

        int amount = freeItemInfo.getAmount();
        if(amount<0){
            return "error";
        }

        PoolAccount poolAccount = poolAccountRepository.findById(freeItemInfo.getPriceId()).orElse(new PoolAccount());

        double finalPrice = price * amount;
        String answer;
        switch (freeItemInfo.getPriceId()){
            case"mensa":
                    if(c.getBalanceMensa()>=finalPrice ){
                        c.setBalanceMensa(c.getBalanceMensa()-finalPrice);
                        answer = "donated succesfully";
                        poolAccount.setBalance(poolAccount.getBalance()+finalPrice);
                        break;
                    }else {
                        answer = "insufficient balance";
                        break;
                    }



            case"shuttle":
                    if(c.getBalanceShuttle()>=finalPrice ){
                        c.setBalanceShuttle(c.getBalanceShuttle()-finalPrice);
                        answer = "donated succesfully";
                        poolAccount.setBalance(poolAccount.getBalance()+finalPrice);
                        break;
                    }else {
                        answer = "insufficient balance";
                        break;
                    }

            default:

                answer = "price not found";
                break;


        }
            poolAccountRepository.save(poolAccount);
            customerRepository.save(c);
            logTransaction(customerId,"Donate",answer,freeItemInfo.getPriceId(),amount);
        return answer;
    }



    //Urunlerin fiyatlarini ogrenmek icin kullanilan fonksiyon
            @ApiOperation(value = "Ürün fiyatı öğrenmek için fonksiyon")
            @ApiResponses(value = {
                    @ApiResponse(code = 200, message = "(price not found) = priceId değişkeni hatalı\n"+
                                                       "bir double sayısı = istenilen fiyat\n"),

                    @ApiResponse(code = 403, message = "Hatalı token bilgisi")
            })
    @RequestMapping(value = "/price-check", method = RequestMethod.POST)
    private String priceCheck(@RequestBody PriceCheck priceCheck){
        String priceId = priceCheck.getPriceId();
        String status = priceCheck.getStatus();
        int amount = priceCheck.getAmount();

        double price = getPrice(priceId,status);

        if (price==-1){
            return "price not found";
        }

        Double finalPrice = price*amount;




        return finalPrice.toString();
    }



    //Bugun odeme yapan kisiler listesi
    private ArrayList<String> customersWhoPaidToday(){
        ArrayList<String> customerListWhoPaid = new ArrayList<>(transactionRepository.getCustomerWhoPaidToday());
        return customerListWhoPaid;
    }


    //Todo: ayni kisi gun icinde ikinci yemeginde fazla para oder
    private String roleDecider(){
        return null;
    }



    //Ucretsiz hizmet alabiliyor mu diye cesitli bilgileri sorgular
    //true veya false dondurur
    //Bagis havuzunda para olmali, kisi ucretsiz hizmet alabilmeli, ayni gun baska hizmet kullanmamis olmalı
    private boolean checkFreeItem(String customerId, String itemId){
        //can this person get free item
        boolean canGetFree = customerRepository.findById(customerId).orElse(new Customer()).isCanGetFreeItem();

        //is this person paid today
        boolean boughtToday = customersWhoPaidToday().contains(customerId);

        //is there enough money in donation pool account
        PoolAccount poolAccount = poolAccountRepository.findById(itemId).orElse(new PoolAccount());
        double price = getPrice(itemId,"Student");
        boolean enoughMoney =poolAccount.getBalance()>=10+price;

        if (enoughMoney && !boughtToday && canGetFree){
            return true;
        }
        return false;
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


    //Islem kaydi yapan fonksiyon
    private void logTransaction(String customerId, String type, String transactionStatus,
                                String balanceId, double amount){

        Date time = new Date();

        Transaction tr = new Transaction(customerId,type,transactionStatus,balanceId,amount,time);

        transactionRepository.save(tr);
    }

    //Urun tablosundan urun bulur
    private double getPrice(String priceId, String customerStatus){
        double price = -1;
        Iterable<Price> prices = priceRepository.findAll();

        for (Price p: prices ){

            if (p.getType().equals(priceId) && p.getCustomerStatus().equals(customerStatus)){
                price = p.getPrice();
                break;
            }
        }
        return price;
    }
}






//Para Transferi bilgilerini saklamak için gecici sinif
class moneyTransferInfo{
    public String receiverId;
    public String balanceId;
    public int amount;

    String getReceiverId() {
        return receiverId;
    }
    String getBalanceId(){return balanceId;}
    int getAmount() {
        return amount;
    }
}


//Odeme methodu için gecici sinif
class PayType{
    public String priceId;

    String getPriceId() {
        return priceId;
    }
}



//Para yukleme methodu icin gecici sinif
class DepositInfo{
    public String balanceId;
    public int amount;

    String getBalanceId(){return balanceId;}
    int getAmount() {
        return amount;
    }
}



//Sifre degistirmek icin gecici sinif
class PasswordInfo{
    public String oldPass;
    public String newPass;

    public String getOldPass() {
        return oldPass;
    }

    String getNewPass(){
        return newPass;
    }
}



//Barkod Okuyucudan gelen kodu almak icin sinif
class QrCodeJsonParser{
    public String qrCode;
    public String priceId;

    public QrCodeJsonParser(){ }
    public QrCodeJsonParser(String qrCode){
        this.qrCode = qrCode;
    }
    String getQrCode() {
        return qrCode;
    }
    String getPriceId(){
        return priceId;
    }
}



//Odendi mi sorgusu icin json sinifi
class IsPaidInfo{
    public String qrCode;

    String getQrCode() {
        return qrCode;
    }
}




//Id bilgisini iceren sinif json icindir
class IdInfo{
    public String id;

    public String getId() {
        return id;
    }
}




//Feedback bilgisini tasiyan sinif
class FeedbackInfo{
    public int star;
    public String type;
    public String text;

    int getStar() {
        return star;
    }
    String getType(){return type;}
    String getText() {
        return text;
    }
}


//Urun fiyatini orenmek icin json dan okunan sinif
class PriceCheck{
    public String priceId;
    public String status;
    public int amount;

    String getPriceId() {
        return priceId;
    }
    String getStatus() {
        return status;
    }
    int getAmount() {
        return amount;
    }
}



//Ucretsiz yaralanamak icin json sinifi
class FreeItemInfo{
    public String priceId;
    public int amount;

    int getAmount() {
        return amount;
    }
    String getPriceId() {
        return priceId;
    }
}