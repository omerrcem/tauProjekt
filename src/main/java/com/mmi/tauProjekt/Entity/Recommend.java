package com.mmi.tauProjekt.Entity;

public class Recommend{
    private String customerId;
    private int recommendTimes=1;

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