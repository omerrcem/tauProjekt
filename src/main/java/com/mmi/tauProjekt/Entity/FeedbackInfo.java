package com.mmi.tauProjekt.Entity;

import java.util.ArrayList;


public class FeedbackInfo {
    
      String id;
      int star;
      String feedbackText;

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
