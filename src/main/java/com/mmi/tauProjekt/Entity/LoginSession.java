package com.mmi.tauProjekt.Entity;

import org.hibernate.validator.constraints.CodePointLength;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "login_session")
public class LoginSession {
    @Id
    @Column(name = "customerid")
    String customerId;
    @Column(name = "login_token")
    String loginToken;
    @Column(name = "last_login_date")
    Date lastLoginDate;
    @Column(name = "last_login_ip")
    String lastLoginIp;

    public LoginSession() {
    }

    public LoginSession(String customerId, String loginToken, Date lastLoginDate, String lastLoginIp) {
        this.customerId = customerId;
        this.loginToken = loginToken;
        this.lastLoginDate = lastLoginDate;
        this.lastLoginIp = lastLoginIp;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getLoginToken() {
        return loginToken;
    }

    public void setLoginToken(String loginToken) {
        this.loginToken = loginToken;
    }

    public Date getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(Date lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }
}
