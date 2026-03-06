package com.bicosteve.api_gateway.security;

import com.bicosteve.api_gateway.repository.JdbcProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final JdbcProfileRepository profileRepository;


    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException{
        return this.profileRepository.findByPhoneNumber(phoneNumber)
                .map(profile -> User.builder()
                        .username(profile.getPhoneNumber())
                        .password(profile.getPassword())
                        .roles("USER")
                        .build()
                ).orElseThrow(()-> new UsernameNotFoundException("Profile for %s not found".formatted(phoneNumber)));
    }
}
