package com.sourabh.sample_auth.Config;

import com.sourabh.sample_auth.Entity.User;
import com.sourabh.sample_auth.Repository.UserRepositiory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CustomuserDetailsService implements UserDetailsService {

    @Autowired
    UserRepositiory userRepositiory;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepositiory.findOneByUserName(username);

        return user.map(CustomUserDetails::new)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username in DB"));
    }
}
