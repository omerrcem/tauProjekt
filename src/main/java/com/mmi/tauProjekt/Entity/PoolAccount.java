package com.mmi.tauProjekt.Entity;

import javax.persistence.*;


//Bagislardan gelen paralar buraya yuklenir

@Entity
@Table(name = "pool_account")
public class PoolAccount {

    @Id
    @Column(name = "poolid")
    String poolid;

    @Column(name = "balance",precision = 10, scale = 2)
    double balance;

    public PoolAccount() {
    }

    public PoolAccount(String poolid, double balance) {
        this.poolid = poolid;
        this.balance = balance;
    }

    public String getPoolid() {
        return poolid;
    }

    public void setPoolid(String poolid) {
        this.poolid = poolid;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}
