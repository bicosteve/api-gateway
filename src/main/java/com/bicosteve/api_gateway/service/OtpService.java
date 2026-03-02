package com.bicosteve.api_gateway.service;

import com.bicosteve.api_gateway.exceptions.InvalidOtpException;
import com.bicosteve.api_gateway.exceptions.OtpExpiredException;
import com.bicosteve.api_gateway.utils.AppUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class OtpService {
    private final StringRedisTemplate redisTemplate;
    private static final int OTP_EXPIRY_HOURS = 5;

    public String generateAndStoreOtp(String phoneNumber){
        String otp = AppUtils.generateOTP();
        this.redisTemplate.opsForValue().set(
                "otp:%s".formatted(phoneNumber),
                otp,
                OTP_EXPIRY_HOURS,
                TimeUnit.HOURS
        );

        return otp;
    }

    public boolean verifyOtp(String phoneNumber, String otp){
        String storedOtp = this.redisTemplate.opsForValue().get("otp:%s".formatted(phoneNumber));
        if(storedOtp == null){
            throw new OtpExpiredException("OTP does not exist");
        }

        if(!storedOtp.equals(otp)){
            throw new InvalidOtpException("Invalid otp");
        }

        this.redisTemplate.delete("otp:%s".formatted(phoneNumber));

        return true;
    }
}
