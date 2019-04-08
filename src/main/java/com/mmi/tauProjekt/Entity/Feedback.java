package com.mmi.tauProjekt.Entity;

public class Feedback {
    private String customerId;
    private int star;
    private String text;

    public Feedback(String customerId, int star, String text) {
        this.customerId = customerId;
        this.star = star;
        this.text = text;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
