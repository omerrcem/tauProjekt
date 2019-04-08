
package com.mmi.tauProjekt.Lists;


import java.util.ArrayList;

import com.mmi.tauProjekt.Entity.FeedbackInfo;

public class FeedbackList {
    
    ArrayList<FeedbackInfo> feedbackList = new ArrayList();
    
    
    public void addFeedback(String id,int star,String feedbackText) {
        FeedbackInfo infos= new FeedbackInfo(id,star,feedbackText);
        feedbackList.add(infos);
       
    }
}
