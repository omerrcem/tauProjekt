package com.mmi.tauProjekt.QrCode;

import java.util.HashMap;
import java.util.UUID;

//
// Code created by Aybuke

public class UserPaymentToken {
    HashMap<String, String> userTokens = new HashMap<String, String>();


    //kullanici odeme yapacagı zaman client bu fonksiyonu cagirir
    //
    public String getPaymentToken(String id) {
        UUID uuid = UUID.randomUUID();
        String qrCode = uuid.toString();
        userTokens.put(qrCode, id);
        return qrCode;
    }



    //Barkod okuyucudan gelen kod burada var mi diye kontrol edilir
    //
    public String confirmPaymentToken(String qrcode) {

        if (userTokens.containsKey(qrcode)) {
            return userTokens.get(qrcode);
        } else {
            return null;
        }

    }

}