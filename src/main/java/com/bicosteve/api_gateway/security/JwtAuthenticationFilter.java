package com.bicosteve.api_gateway.security;

import com.bicosteve.api_gateway.utils.LogContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;


    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException{

        // 01. Generate unique traceId for every request
        String traceId = UUID.randomUUID().toString().substring(0, 8);
        LogContext.setTraceId(traceId);

        // 02. Add traceId to response header so that client can correlate
        response.setHeader("X-Trace-Id", traceId);

        try{
            // 03. Get authorization header
            String authHeader = request.getHeader("Authorization");

            // 04. Check if header exists and starts with <Bearer >
            if(authHeader == null || !authHeader.startsWith("Bearer ")){
                filterChain.doFilter(request,response);
                return;
            }

            // 05. Replace "Bearer " with an empty string ""
            String token = authHeader.replace("Bearer ","");

            // 06. Validate the token
            if(!this.jwtService.validateToken(token)){
                filterChain.doFilter(request,response);
                return;
            }

            // 07. Get PhoneNumber, ProfileId from token
            String phoneNumber = this.jwtService.getPhoneNumberFromToken(token);

            // 08. Load user and set authentication in SecurityContext
            if(phoneNumber != null && SecurityContextHolder.getContext().getAuthentication() == null){
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(phoneNumber);
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }

            filterChain.doFilter(request,response);

        } finally {
            LogContext.clear();
        }


    }
}
