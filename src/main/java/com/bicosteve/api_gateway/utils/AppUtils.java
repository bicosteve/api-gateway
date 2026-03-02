package com.bicosteve.api_gateway.utils;


import java.security.SecureRandom;

public class AppUtils {

    private AppUtils(){}

    // Generate otp
    public static String generateOTP(){
        SecureRandom random = new SecureRandom();
        int otp = 100_000 + random.nextInt(900_000);
        return String.valueOf(otp);
    }
}
