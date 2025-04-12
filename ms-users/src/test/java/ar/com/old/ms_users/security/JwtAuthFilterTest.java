package ar.com.old.ms_users.security;

import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.enumerations.Role;
import io.jsonwebtoken.JwtException;
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
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtAuthFilterTest {

    public static final String AUTHORIZATION = "Authorization";
    @InjectMocks
    private JwtAuthFilter jwtAuthFilter;
    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private FilterChain filterChain;
    @Mock
    private JwtService jwtService;
    @Mock
    private CustomUserDetailsService userDetailsService;

    @Test
    void shouldSkipAuthentication_whenHasAuthEndpoint() throws ServletException, IOException {
        //GIVEN
        when(request.getRequestURI()).thenReturn("/api/auth/test");

        //WHEN
        jwtAuthFilter.doFilterInternal(request,response,filterChain);

        //THEN
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request,response);
    }

    @Test
    void shouldNotAuthenticate_whenTokenIsMissing() throws ServletException, IOException {
        //GIVEN
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader(AUTHORIZATION)).thenReturn(null);

        //WHEN
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        //THEN
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request,response);
    }

    @Test
    void shouldReturnErrorGettingUserName_whenHasInvalidToken() throws ServletException, IOException {
        //GIVEN
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader(AUTHORIZATION)).thenReturn("Bearer xxx");
        when(response.getWriter()).thenReturn(new PrintWriter(Writer.nullWriter()));
        when(jwtService.getSubject(anyString())).thenThrow(new JwtException("test exception"));

        //WHEN
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        //THEN
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(response).getWriter();
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void shouldReturnErrorValidatingToken_whenHasInvalidToken() throws ServletException, IOException {
        //GIVEN
        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader(AUTHORIZATION)).thenReturn("Bearer xxx");
        when(response.getWriter()).thenReturn(new PrintWriter(Writer.nullWriter()));
        when(jwtService.getSubject(anyString())).thenReturn("username");
        when(userDetailsService.loadUserByUsername("username")).thenReturn(new CustomUserDetails(new User()));
        when(jwtService.isValid(anyString(),any(UserDetails.class))).thenThrow(new JwtException("test exception"));

        //WHEN
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        //THEN
        assertNull(SecurityContextHolder.getContext().getAuthentication());

        verify(response).getWriter();
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    void shouldAuthenticateUser_whenTokenIsValid() throws ServletException, IOException {
        //GIVEN
        User user = new User(1L, "test", "pass1234", "mail@mail.com");
        user.setRole(Role.USER);
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        when(request.getRequestURI()).thenReturn("/api/test");
        when(request.getHeader(AUTHORIZATION)).thenReturn("Bearer xxx");
        when(jwtService.getSubject(anyString())).thenReturn("username");
        when(userDetailsService.loadUserByUsername("username")).thenReturn(customUserDetails);
        when(jwtService.isValid(anyString(), any(UserDetails.class))).thenReturn(true);

        //WHEN
        jwtAuthFilter.doFilterInternal(request, response, filterChain);

        //THEN
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());

        verify(filterChain).doFilter(request,response);
    }

}