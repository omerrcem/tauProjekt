package com.mmi.tauProjekt.Entity;

//Hizmet ve urunlerin ucretlerini saklayan obje
public class Price {
    private String id;
    private String customerStatus;
    private double price;

    public Price(String id, String customerStatus,double price) {
        this.id = id;
        this.customerStatus=customerStatus;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCustomerStatus() {
        return customerStatus;
    }

    public void setCustomerStatus(String customerStatus) {
        this.customerStatus = customerStatus;
    }
}
