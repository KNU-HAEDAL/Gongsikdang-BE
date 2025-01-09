package com.food.dto;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

@Getter
@Setter
@Builder
@ToString
public class UserDTO implements UserDetails {
    private String id;
    private String password;
    private String name;
    private int phone;
    private Collection<? extends GrantedAuthority> authorities;

    // Constructor
    public UserDTO() {
    }

    public UserDTO(String id, String password, String name, int phone, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.authorities = authorities;
    }

    // UserDetails interface methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return id;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}