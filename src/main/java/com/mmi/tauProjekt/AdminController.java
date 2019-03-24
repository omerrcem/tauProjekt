package com.mmi.tauProjekt;

import com.mmi.tauProjekt.Entity.Customer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private CustomerList customerList;
    @Autowired
    private AdminList adminList;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    public AdminController(CustomerList CustomerList, AdminList adminList, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.customerList = CustomerList;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.adminList=adminList;
    }


    //
    //Admin panelinde ogrenci bilgilerini degistirmeye yarar
    //Json icerisinde Customer classına uygun bir body olmalı
    //Admin tokeni gerektirir
    //
    @RequestMapping(value = "/edit-customer-info", method = RequestMethod.POST)
    public void editCustomerInfo(@RequestBody Customer targetCustomer) {
        Customer s =customerList.getCustomer(targetCustomer.getId());
        if (s == null){
            throw  new UsernameNotFoundException(targetCustomer.getId());
        }
        if (!targetCustomer.getId().equals("")){
            s.setId(targetCustomer.getId());
        }
        if (targetCustomer.getBalance()!=-1){
            s.setBalance(targetCustomer.getBalance());
        }
        if (!targetCustomer.getName().equals("")){
            s.setName(targetCustomer.getName());
        }
        if (!targetCustomer.getMail().equals("")){
            s.setMail(targetCustomer.getMail());
        }
        if(!targetCustomer.getPassword().equals("")){
            s.setPassword(bCryptPasswordEncoder.encode(targetCustomer.getPassword()));
        }
    }

    //
    //Ogrenci silmeye yarar
    //
    @RequestMapping(value = "/delete-customer", method = RequestMethod.POST)
    public void deleteCustomer(@RequestBody CustomerIdInfo CustomerIdInfo){
        String id = CustomerIdInfo.getCustomerId();
        if (customerList.getCustomer(id) == null){
            throw new UsernameNotFoundException(id);
        }
        customerList.deleteCustomer(id);

    }

}

class CustomerIdInfo{
    private String customerId;

    public String getCustomerId() {
        return customerId;
    }
}


