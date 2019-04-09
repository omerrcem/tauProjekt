package com.mmi.tauProjekt.Entity;

import javax.persistence.*;
import java.util.ArrayList;

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
    @Column(name = "text")
    private String feedbackText;

    public FeedbackInfo(String id, int star, String feedbackText) {
        this.id = id;
        this.star = star;
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
    
   
    
}
