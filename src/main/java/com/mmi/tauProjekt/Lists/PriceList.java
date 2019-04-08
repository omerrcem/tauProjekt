package com.mmi.tauProjekt.Lists;

import com.mmi.tauProjekt.Entity.Price;


import java.util.ArrayList;
import java.util.Arrays;


//SQL Gelince silinecek
//Hizmet ve urunlerin saklandigi sayfa

public class PriceList {

    ArrayList<Price> prices = new ArrayList(Arrays.asList(

            new Price("mensa","Student", 3.5),
            new Price("mensa","Personal", 4),
            new Price ("shuttle","Student", 2),
            new Price ("shuttle","Personal", 2)
    ));

    public PriceList(){ }

    public double getPrice(String status,String id){

        for(Price p : prices){

            if (p.getId().equals(id) && p.getCustomerStatus().equals(status)){
                return p.getPrice();
            }

        }
        return -1;
    }

}
