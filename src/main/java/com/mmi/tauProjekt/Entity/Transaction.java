package com.mmi.tauProjekt.Entity;

import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.util.Date;

//SQL tablosunda islemleri saklamak icin kullanilan sinif

@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transactionid")
    int transactionId;

    @Column(name = "customerid")
    String customerId;

    @Column(name = "type")
    String type;

    @Column(name = "transactionstatus")
    String transactionStatus;

    @Column(name = "balanceid")
    String balanceId;

    @Column(name = "amount")
    double amount;

    @Column(name = "time")
    @DateTimeFormat(pattern = "dd-MM-yyyy hh:mm")
    private Date time;

    public Transaction() {
    }

    public Transaction(  String customerId, String type,
                       String transactionStatus, String balanceId, double amount, Date time) {

        this.customerId = customerId;
        this.type = type;
        this.transactionStatus = transactionStatus;
        this.balanceId = balanceId;
        this.amount = amount;
        this.time = time;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(String transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getBalanceId() {
        return balanceId;
    }

    public void setBalanceId(String balanceId) {
        this.balanceId = balanceId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public Date getTime() {
        return time;
    }

    public void setTime(Date time) {
        this.time = time;
    }
}
