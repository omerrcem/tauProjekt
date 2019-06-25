package com.mmi.tauProjekt.Repository;

import com.mmi.tauProjekt.Entity.Ad;
import com.mmi.tauProjekt.Entity.Customer;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Collection;

@Repository
@Transactional
public interface AdRepository extends CrudRepository<Ad,Integer> {

    @Query("SELECT ad.adId FROM Ad ad WHERE :customerid = ad.customerId")
    Collection<Integer> findCustomerAd(@Param("customerid" )String customerId);
}