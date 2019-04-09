package com.mmi.tauProjekt.Repository;

import com.mmi.tauProjekt.Entity.Recommend;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecommendRepository extends CrudRepository<Recommend,String> {
}
