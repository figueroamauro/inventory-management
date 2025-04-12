package ar.com.old.ms_users.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;


    @Test
    void shouldSkipAuthentication_whenHasAuthEndpoint() throws ServletException, IOException {
        //GIVEN
        when(request.getRequestURI()).thenReturn("/api/auth/test");

        //WHEN
        jwtAuthFilter.doFilterInternal(request,response,filterChain);

        //THEN
        verify(filterChain).doFilter(request,response);
    }

    @Test
    void shouldNotAuthenticate_whenTokenIsMissing() throws ServletException, IOException {
        //GIVEN
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader("Authorization")).thenReturn(null);

        //WHEN
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        //THEN
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request,response);
    }

}