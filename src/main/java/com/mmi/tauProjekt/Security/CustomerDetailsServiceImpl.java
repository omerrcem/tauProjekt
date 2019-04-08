package com.mmi.tauProjekt.Security;

import com.mmi.tauProjekt.Lists.CustomerList;
import com.mmi.tauProjekt.Entity.Customer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.Collections.emptyList;

@Service
public class CustomerDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private CustomerList list;

    @Autowired

    public CustomerDetailsServiceImpl() {

    }

    //login olunca bu kisimda kullanici var mi diye kontrol edilir
    //daha sonra buraya sql baglanacak
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer s = list.getCustomer(username);
        if (s == null) {

                throw new UsernameNotFoundException(username);

        }

       return new User(s.getId(), s.getPassword(), emptyList());
    }


}
