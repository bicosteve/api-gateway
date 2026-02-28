package com.bicosteve.api_gateway.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class Profile {
    private Long profileId;
    private String PhoneNumber;
    private String PasswordHash;
    private Date CreatedAt;
    private Date ModifiedAt;
}
