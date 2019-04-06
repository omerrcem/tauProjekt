package com.mmi.tauProjekt.Entity;


//Ogrenci bilgilerini saklayan obje
//Sifreler her zaman crypted bir sekilde saklanir
public class Customer {

    private String id;
    private String name;
    private String mail;
    private String password;
    private String status;

    private int balanceMensa = 0;
    private int balanceShuttle = 0;
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

    public int getBalanceMensa() {
        return balanceMensa;
    }

    public void setBalanceMensa(int balanceMensa) {
        this.balanceMensa = balanceMensa;
    }

    public int getBalanceShuttle() {
        return balanceShuttle;
    }

    public void setBalanceShuttle(int balanceShuttle) {
        this.balanceShuttle = balanceShuttle;
    }
}
