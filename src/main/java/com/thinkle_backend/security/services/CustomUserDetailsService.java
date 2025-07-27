package com.thinkle_backend.security.services;

import com.thinkle_backend.models.ThinkleUsers;
import com.thinkle_backend.repositories.ThinkleUsersRepository;
import com.thinkle_backend.security.models.UserPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final ThinkleUsersRepository thinkleUsersRepository;

    public CustomUserDetailsService(ThinkleUsersRepository thinkleUsersRepository) {
        this.thinkleUsersRepository = thinkleUsersRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // loading by unique emails
        Optional<ThinkleUsers> thinkleUsersOptional = this.thinkleUsersRepository.findByEmail(email);

        if(thinkleUsersOptional.isEmpty()){
            throw new UsernameNotFoundException("This user with email: " + email + " is not present");
        }

        ThinkleUsers thinkleUsers = thinkleUsersOptional.get();

        return new UserPrincipal(thinkleUsers);
    }
}
