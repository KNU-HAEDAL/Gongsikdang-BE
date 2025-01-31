package com.food.dto;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;

@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
public class UserDTO implements UserDetails {
    private String id;
    private String password;
    private String name;
    private Integer point;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDTO(String id, String password, String name, Integer point, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.password = password;
        this.name = name;
        this.point = point;
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