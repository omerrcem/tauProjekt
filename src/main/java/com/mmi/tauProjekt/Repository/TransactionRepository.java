package com.mmi.tauProjekt.Repository;

import com.mmi.tauProjekt.Entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface TransactionRepository extends CrudRepository<Transaction,Integer> {


    @Query("SELECT distinct t.customerId FROM Transaction t WHERE t.time <= NOW() AND t.time > CURDATE() AND t.transactionStatus='paid successfully' OR t.transactionStatus='received free item' AND t.type='Pay'")
    Collection<String> getCustomerWhoPaidToday();
}
