package com.mmi.tauProjekt.Repository;

import com.mmi.tauProjekt.Entity.Price;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PriceRepository extends CrudRepository<Price, Integer> {



}
