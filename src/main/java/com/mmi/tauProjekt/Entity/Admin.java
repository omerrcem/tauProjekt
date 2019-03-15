package com.mmi.tauProjekt.Entity;

public class Admin {

    private String adminId;
    private String name;
    private String mail;
    private String password;


    public Admin(String adminId, String name, String mail, String password) {
        this.adminId = adminId;
        this.name = name;
        this.mail = mail;
        this.password = password;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
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
}
