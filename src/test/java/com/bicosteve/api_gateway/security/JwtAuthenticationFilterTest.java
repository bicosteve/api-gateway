package com.bicosteve.api_gateway.security;

import com.bicosteve.api_gateway.models.Profile;
import com.bicosteve.api_gateway.models.ProfileSettings;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class JwtAuthenticationFilterTest {

    private JwtService jwtService;
    private UserDetailsServiceImpl userDetailsService;
    private JwtAuthenticationFilter filter;
    private com.bicosteve.api_gateway.repository.ProfileRepository profileRepository;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
        JwtConfig jwtConfig = new JwtConfig();
        jwtConfig.setSecret("0123456789abcdef0123456789abcdef0123456789abcdef0123456789abcdef");
        jwtConfig.setAccessTokenExpiration(60);
        jwtConfig.setRefreshTokenExpiration(3600);

        jwtService = new JwtService(jwtConfig);

        profileRepository = mock(com.bicosteve.api_gateway.repository.ProfileRepository.class);
        userDetailsService = new UserDetailsServiceImpl(profileRepository);

        filter = new JwtAuthenticationFilter(jwtService, userDetailsService);
    }

    @Test
    void filterWithoutAuthorizationHeaderPassesThrough() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNotNull(response.getHeader("X-Trace-Id"));
    }

    @Test
    void filterWithInvalidAuthorizationHeaderPassesThrough() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Basic abc");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void filterWithInvalidTokenPassesThrough() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer not-a-valid-token");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void filterWithValidTokenSetsAuthentication() throws ServletException, IOException {
        ProfileSettings settings = ProfileSettings.builder()
                .status(1).isVerified(1).isDeleted(0).build();
        Profile profile = Profile.builder()
                .profileId(11L)
                .phoneNumber("254701234567")
                .password("hash")
                .profileSettings(settings)
                .build();
        when(profileRepository.findByPhoneNumber("254701234567"))
                .thenReturn(Optional.of(profile));

        String token = jwtService.generateAccessToken(profile);

        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
        assertEquals("254701234567",
                SecurityContextHolder.getContext().getAuthentication().getName());
    }

    @Test
    void filterClearsMdcAfterRequest() throws ServletException, IOException {
        MockHttpServletRequest request = new MockHttpServletRequest();
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        assertNull(org.slf4j.MDC.get("traceId"));
    }
}
