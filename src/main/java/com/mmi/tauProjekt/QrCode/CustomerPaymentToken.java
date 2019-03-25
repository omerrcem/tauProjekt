package com.mmi.tauProjekt.QrCode;

import java.util.Date;
import java.util.HashMap;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

//
// Code created by Aybuke

public class CustomerPaymentToken {
    HashMap<String, PaymentInfo> userTokens = new HashMap<String, PaymentInfo>();


    //kullanici odeme yapacagı zaman client bu fonksiyonu cagirir
    //
    public String getPaymentToken(String id) {
        UUID uuid = UUID.randomUUID();
        String qrCode = uuid.toString();
        userTokens.put(qrCode, new PaymentInfo(id));


        //Bu thread belirtilen sure sonra otomatik olarak anahtari siler
        Thread autoRemove = new Thread(){
            public void run(){
                try {

                    TimeUnit.SECONDS.sleep(120);

                    if (userTokens.containsKey(qrCode)) {
                        userTokens.remove(qrCode);
                    }


                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        autoRemove.start();
        return qrCode;
    }



    //Client her saniye odeme yapilip yapilmadigini kontrol eder
    public boolean isPaid(String qrcode){

        if (userTokens.containsKey(qrcode)){
            return userTokens.get(qrcode).isPaid;
        }else {
            return false;
        }
    }





    //Barkod okuyucudan gelen kod burada var mi diye kontrol edilir
    //
    public String confirmPaymentToken(String qrcode) {

        if (userTokens.containsKey(qrcode) && !userTokens.get(qrcode).isPaid) {
            userTokens.get(qrcode).isPaid = true;
            System.out.println("paid!");
            return userTokens.get(qrcode).getId();
        } else {
            return null;
        }

    }



    //Qr Code manuel silme methodu
    //
    public void deletePaymentToken(String qrCode) {
        if (userTokens.containsKey(qrCode) == true) {
            userTokens.remove(qrCode);

        }

    }

}

class PaymentInfo{
    String id;
    boolean isPaid = false;
    Date createDate;

    public PaymentInfo(String id){
        this.id= id;
        createDate = new Date();
    }

    public String getId() {
        return id;
    }
}