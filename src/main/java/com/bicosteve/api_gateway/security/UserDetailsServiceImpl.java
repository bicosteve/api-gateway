package com.bicosteve.api_gateway.security;

import com.bicosteve.api_gateway.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final ProfileRepository profileRepository;


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
