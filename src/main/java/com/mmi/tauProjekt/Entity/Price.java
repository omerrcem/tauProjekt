package com.mmi.tauProjekt.Entity;


import javax.persistence.*;

//Hizmet ve urunlerin ucretlerini saklayan obje
@Entity
@Table(name = "price")
public class Price {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private int id;
    @Column(name = "type")
    private String type;
    @Column(name = "customerstatus")
    private String customerStatus;
    @Column(name = "price")
    private double price;

    public Price(){}

    public Price(String type, String customerStatus,double price) {
        this.type = type;
        this.customerStatus=customerStatus;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String id) {
        this.type = type;
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
