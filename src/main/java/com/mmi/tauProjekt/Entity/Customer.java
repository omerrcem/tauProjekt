package com.mmi.tauProjekt.Entity;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

//Ogrenci bilgilerini saklayan obje
//Sifreler raw olarak saklanir
//
@Entity
@Table(name = "customer")
public class Customer {

    @Id
    @Column(name = "personid")
    private String id;
    @Column(name = "name")
    private String name;
    @Column(name = "mail")
    private String mail;
    @Column(name = "password")
    private String password;
    @Column(name = "status")
    private String status;

    @Column(name = "cangetfreeitem")
    private boolean canGetFreeItem;

    @Column(name = "balancemensa")
    private double balanceMensa ;
    @Column(name = "balanceshuttle")
    private double balanceShuttle;

    public Customer(){

    }

    public Customer(String id, String password){
        this.id = id;
        this.password = password;
    }

    public Customer(String id, String name, String mail, String password, String status) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.password = password;
        this.status = status;
    }

    public Customer(String id, String name, String mail, String password, String status,
                    boolean canGetFreeItem, double balanceMensa, double balanceShuttle) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.password = password;
        this.status = status;
        this.canGetFreeItem = canGetFreeItem;
        this.balanceMensa = balanceMensa;
        this.balanceShuttle = balanceShuttle;
    }

    @Override
    public boolean equals(Object obj) {
        Customer s = (Customer) obj;
        if (this.id.equals(s.getId()) && this.password.equals(s.getId())){
            return true;
        }else {
            return false;
        }
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getBalanceMensa() {
        return balanceMensa;
    }

    public void setBalanceMensa(double balanceMensa) {
        this.balanceMensa = balanceMensa;
    }

    public double getBalanceShuttle() {
        return balanceShuttle;
    }

    public void setBalanceShuttle(double balanceShuttle) {
        this.balanceShuttle = balanceShuttle;
    }

    public boolean isCanGetFreeItem() {
        return canGetFreeItem;
    }

    public void setCanGetFreeItem(boolean canGetFreeItem) {
        this.canGetFreeItem = canGetFreeItem;
    }
}
