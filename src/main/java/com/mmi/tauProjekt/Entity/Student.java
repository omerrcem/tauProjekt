package com.mmi.tauProjekt.Entity;


//Ogrenci bilgilerini saklayan obje
//Sifreler her zaman crypted bir sekilde saklanir
public class Student {

    private String id;
    private String name;
    private String mail;
    private String password;

    private int balance = 0;
    public Student(){

    }

    public Student(String id, String password){
        this.id = id;
        this.password = password;
    }

    public Student(String id, String name, String mail, String password) {
        this.id = id;
        this.name = name;
        this.mail = mail;
        this.password = password;
    }


    @Override
    public boolean equals(Object obj) {
        Student s = (Student) obj;
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

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }
}
