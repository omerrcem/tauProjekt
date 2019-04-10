package com.mmi.tauProjekt.Entity;

import javax.persistence.*;
import java.util.ArrayList;


//Feedback bilgissini saklayan sinif
@Entity
@Table(name = "feedback")
public class FeedbackInfo {


    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "feedbackid", updatable = false, nullable = false)
    private int feedbackId;

    @Column(name = "customerid")
    private String id;
    @Column(name = "star")
    private int star;
    @Column(name = "type")
    private String type;
    @Column(name = "text")
    private String feedbackText;

    public FeedbackInfo(String id, int star, String type, String feedbackText) {
        this.id = id;
        this.star = star;
        this.type=type;
        this.feedbackText = feedbackText;
        System.out.println("Feedback eklendi");
    }
      
      
      public String getId() {
          return id;
      }
      public void setId(String id) {
          this.id=id;
      }
      public int getStar() {
          return star;
      }
      public void setStar(int star) {
          this.star=star;
      }
      public String getFeedbackText() {
          return feedbackText;
          
      }
      public void setFeedbacktext(String feedbackText) {
          this.feedbackText=feedbackText;
      }

    public int getFeedbackId() {
        return feedbackId;
    }

    public void setFeedbackId(int feedbackId) {
        this.feedbackId = feedbackId;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFeedbackText(String feedbackText) {
        this.feedbackText = feedbackText;
    }
}
