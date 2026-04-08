package com.bicosteve.api_gateway.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;

@Getter
public class CustomUserDetails extends User{
    private final Long profileId;
    private final String phoneNumber;
    private final Integer status;
    private final Integer isVerified;
    private final Integer isDeleted;

    public CustomUserDetails(
            Long profileId,
            String phoneNumber,
            Integer status,
            Integer isVerified,
            Integer isDeleted,
            String password,
            Collection<? extends GrantedAuthority> authorities
    ){
        super(phoneNumber, password, authorities);
        this.profileId = profileId;
        this.phoneNumber = phoneNumber;
        this.status = status;
        this.isVerified = isVerified;
        this.isDeleted = isDeleted;
    }

}
