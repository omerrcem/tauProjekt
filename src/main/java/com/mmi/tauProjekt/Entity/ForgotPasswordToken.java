package com.mmi.tauProjekt.Entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;


//Sifremi unuttum sayfasi icin kisiye ozel uretilen token sinifi
//

@Entity
@Table(name = "forgot_password_token")
public class ForgotPasswordToken {
    @Id
    @Column(name = "token")
    String token;

    @Column(name = "customerid")
    String customerId;

    @Column(name = "create_date")
    @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
    private Date createDate;

    public ForgotPasswordToken() {
    }

    public ForgotPasswordToken(String token, String customerId) {
        this.token = token;
        this.customerId = customerId;
        this.createDate = new Date();
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }
}
