package org.example.hrmanagementsystem.security.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.auth.entity.User;
import org.example.hrmanagementsystem.auth.repository.UserRepository;
import org.example.hrmanagementsystem.security.model.MyUserDetails;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername (String username)
        throws UsernameNotFoundException{

        User user = userRepository.findByUsername(username)
                .orElseThrow(()->
                        new UsernameNotFoundException("user not found"));

        if(!user.isActive()) {
            throw new DisabledException("Account is deactivated.");
        }

        return new MyUserDetails(user);

    }
}
