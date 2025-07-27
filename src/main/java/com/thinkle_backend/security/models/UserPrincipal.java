package com.thinkle_backend.security.models;

import com.thinkle_backend.models.ThinkleUsers;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final ThinkleUsers thinkleUsers;

    public UserPrincipal(ThinkleUsers thinkleUsers) {
        this.thinkleUsers = thinkleUsers;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // For now, using only one role
        return Collections.singleton(new SimpleGrantedAuthority("USER"));
    }

    @Override
    public String getPassword() {
        return this.thinkleUsers.getPassword();
    }

    @Override
    public String getUsername() {
        // getting the users by unique emails
        return this.thinkleUsers.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        // for simplicity for now
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // for simplicity for now
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // for simplicity for now
        return true;
    }

    @Override
    public boolean isEnabled() {
        // for simplicity for now
        return true;
    }
}
