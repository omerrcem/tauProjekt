package com.mmi.tauProjekt.Repository;

import com.mmi.tauProjekt.Entity.FeedbackInfo;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends CrudRepository<FeedbackInfo,Integer> {

}
