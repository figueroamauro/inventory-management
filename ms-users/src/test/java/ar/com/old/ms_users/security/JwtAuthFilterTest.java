package ar.com.old.ms_users.security;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.context.SecurityContextHolder;

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
        verify(response).getWriter();
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

}