package com.mmi.tauProjekt.Entity;

import com.mmi.tauProjekt.Mail.MailService;

import javax.mail.MessagingException;
import javax.persistence.*;

@Entity
@Table (name = "recommend")
public class Recommend{

    @Id
    @Column (name= "customerid")
    private String customerId;
    @Column (name = "recommendtimes")
    private int recommendTimes=1;

    public Recommend(){}
    public Recommend(String customerId){
        this.customerId=customerId;
    }

    public void increaseRecommendTime(){
        this.recommendTimes++;
    }


    public String getCustomerId() {
        return customerId;
    }

    public int getRecommendTimes() {
        return recommendTimes;
    }
}
