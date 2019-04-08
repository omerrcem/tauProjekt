package com.mmi.tauProjekt.Lists;

import com.mmi.tauProjekt.Entity.Recommend;

import java.util.ArrayList;
import java.util.Arrays;

public class RecommendList {
    ArrayList<Recommend> recommendList = new ArrayList(Arrays.asList(new Recommend("160503133")));


    public void addRecommend(String customerId){
        for (Recommend r: recommendList){
            if (r.getCustomerId().equals(customerId)){
                r.increaseRecommendTime();
                return;
            }
        }

        recommendList.add(new Recommend(customerId));
        return;
    }


}



