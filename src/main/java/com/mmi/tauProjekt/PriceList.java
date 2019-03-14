package com.mmi.tauProjekt;

import com.mmi.tauProjekt.Entity.Price;


import java.util.ArrayList;
import java.util.Arrays;


//SQL Gelince silinecek
//Hizmet ve urunlerin saklandigi sayfa

public class PriceList {

    ArrayList<Price> prices = new ArrayList(Arrays.asList(

            new Price("mensa", 4),
            new Price ("shuttle", 2)
    ));

    public PriceList(){ }

    public int getPrice(String id){

        for(Price p : prices){

            if (p.getId().equals(id)){
                return p.getPrice();
            }

        }
        return -1;
    }

}
