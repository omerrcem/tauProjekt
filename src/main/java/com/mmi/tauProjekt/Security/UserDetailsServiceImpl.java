package com.mmi.tauProjekt.Security;

import com.mmi.tauProjekt.AdminList;
import com.mmi.tauProjekt.Entity.Admin;
import com.mmi.tauProjekt.Entity.Student;
import com.mmi.tauProjekt.StudentList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static java.util.Collections.emptyList;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private StudentList list;

    @Autowired
    private AdminList adminList;

    public UserDetailsServiceImpl() {

    }

    //login olunca bu kisimda kullanici var mi diye kontrol edilir
    //daha sonra buraya sql baglanacak
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Student s = list.getStudent(username);
        if (s == null) {
            Admin a = adminList.getAdmin(username);
            if ( a == null){
                throw new UsernameNotFoundException(username);
            }

            return new User(a.getAdminId(), a.getPassword(), emptyList());
        }


       return new User(s.getId(), s.getPassword(), emptyList());
    }


}
