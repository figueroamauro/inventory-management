package ar.com.old.ms_users.security;

import ar.com.old.ms_users.exceptions.UserNotFoundException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthFilter extends OncePerRequestFilter {

    public static final String AUTHORIZATION = "Authorization";
    public static final String BEARER = "Bearer ";

    private final JwtService jwtService;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtAuthFilter(JwtService jwtService, CustomUserDetailsService customUserDetailsService) {
        this.jwtService = jwtService;
        this.customUserDetailsService = customUserDetailsService;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (isAuthEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getToken(request);

        if (token != null) {
            try {
                String username = jwtService.getSubject(token);
                UserDetails currentUser = customUserDetailsService.loadUserByUsername(username);
                boolean isValidToken = jwtService.isValid(token, currentUser);

                if (isValidToken) {
                    authenticate(request, token, (CustomUserDetails) currentUser);
                }

            } catch (JwtException e) {
                sendError(response, e.getMessage());
            } catch (UserNotFoundException e) {
                sendError(response, "Invalid token");
            }
        }

        filterChain.doFilter(request, response);
    }

    private static boolean isAuthEndpoint(HttpServletRequest request) {
        return request.getRequestURI().contains("/api/auth");
    }

    private String getToken(HttpServletRequest request) {
        if (hasToken(request)) {
            return request.getHeader(AUTHORIZATION).substring(7);
        } else {
            return null;
        }
    }

    private boolean hasToken(HttpServletRequest request) {
        String authHeader = request.getHeader(AUTHORIZATION);
        return authHeader != null && authHeader.startsWith(BEARER);
    }

    private void authenticate(HttpServletRequest request, String token, CustomUserDetails userDetails) {
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private void sendError(HttpServletResponse response, String message) throws IOException {
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("{\"error\": \"" + message + "\"}");
    }

}
