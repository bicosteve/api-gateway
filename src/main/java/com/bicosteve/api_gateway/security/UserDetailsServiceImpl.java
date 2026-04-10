package com.bicosteve.api_gateway.security;

import com.bicosteve.api_gateway.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final ProfileRepository profileRepository;

    @Override
    public UserDetails loadUserByUsername(String phoneNumber) throws UsernameNotFoundException{
        return this.profileRepository.findByPhoneNumber(phoneNumber)
                .map(profile -> new CustomUserDetails(
                        profile.getProfileId(),
                        profile.getPhoneNumber(),
                        profile.getProfileSettings().getStatus(),
                        profile.getProfileSettings().getIsVerified(),
                        profile.getProfileSettings().getIsDeleted(),
                        profile.getPassword(),
                        List.of(new SimpleGrantedAuthority("ROLE_USER"))
                ))
                .orElseThrow(() -> new UsernameNotFoundException("Profile for %s not found ".formatted(phoneNumber)));
//
    }
}
