package com.lazeez.security;

import com.lazeez.entity.User;
import com.lazeez.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;

@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User  user =  userRepository.findByEmail(username);

        if(user==null)
        {
            throw new UsernameNotFoundException("User Not Found");
        }
        return  new org.springframework.security.core.userdetails.User(user.getEmail(),user.getPassword(), getGrantedAuthorityRoles(user.getRole()));
    }



    private Collection<? extends GrantedAuthority> getGrantedAuthorityRoles(String role)
    {
       return  Collections.singletonList(new SimpleGrantedAuthority(role));
    }


}
