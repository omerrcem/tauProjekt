package com.mmi.tauProjekt.StartUp;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConnectionControl {

    @RequestMapping(value = "/connect")
    public String connectionCheck(){
        return ("<pre>"+StartMessage.welcomeMessage().replaceAll("\n","<br>")+"<br><br>You successfully connected to the server :)<br><br>"+
                "If you see this message that means the server is operating and ready for orders!<br><br>"+
                "Heroes of the backend:<br>"+
                "   Ömer Cem Turan<br>"+
                "   Aybüke Bayramiç<br>"+
                "   Ayşe Rabia Özbek<br>"+
                "   Onur Mer Doğan<br>"+
                "   Onur Burak Kılıç<br>"+
                "   Can Çelikkanat<br><br>"+
                "Tau-Pay Backend Server"+"<pre>");
    }
}
