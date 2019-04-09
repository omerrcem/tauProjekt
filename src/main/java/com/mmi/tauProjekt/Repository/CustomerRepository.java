package com.mmi.tauProjekt.Repository;

import com.mmi.tauProjekt.Entity.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface CustomerRepository extends CrudRepository<Customer,String> {


}
