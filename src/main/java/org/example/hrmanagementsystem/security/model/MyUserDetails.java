package org.example.hrmanagementsystem.security.model;

import lombok.RequiredArgsConstructor;
import org.example.hrmanagementsystem.auth.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class MyUserDetails implements UserDetails {

    private final User user;

    @Override
    public String getUsername() {return user.getUsername();}
    @Override
    public String getPassword() {return user.getPassword();}
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities(){
        return List.of(new SimpleGrantedAuthority(user.getRole().name()));
    }
    @Override
    public boolean isEnabled(){return user.isActive();}

    public Long getEmployeeId(){return user.getEmployee() != null ? user.getEmployee().getEmployeeId() : null;}

    public String getRole(){return user.getRole().name();}
}

