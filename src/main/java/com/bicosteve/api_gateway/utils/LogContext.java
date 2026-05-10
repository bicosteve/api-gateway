package com.bicosteve.api_gateway.utils;

import org.slf4j.MDC;

public class LogContext {
    private static final String TRACE_ID = "traceId";
    private static final String PROFILE_ID = "profileId";
    private static final String PHONE = "phone";

    // Set all context at once
    public static void set(String traceId,String profileId){
        MDC.put(TRACE_ID,traceId != null ? traceId : "no-trace");
        MDC.put(PROFILE_ID,profileId != null ? "profile::" + profileId : "unknown profile");
    }

    // Set traceId for only unauthorized requests
    public static void setTraceId(String traceId){
        MDC.put(TRACE_ID,traceId != null ? traceId : "no-trace");
    }

    // Set profileId only after authentication
    public static void setProfileId(String profileId){
        MDC.put(PROFILE_ID,profileId != null ? "profile::" + profileId : "unknown profile");
    }

    // Set phoneNumber at registration, login, verification
    public static void setPhone(String phone){
        MDC.put(PHONE,phone != null ? phone : "Unknown");
    }

    // Get current traceId. Useful in returning response headers
    public static String getTraceId(){
        return MDC.get(TRACE_ID);
    }

    // Clear all. Has to be called for every request
    public static void clear(){
        MDC.clear();
    }
}
