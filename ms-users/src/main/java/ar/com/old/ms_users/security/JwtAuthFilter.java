package ar.com.old.ms_users.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        if (isAuthEndpoint(request)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = getToken(request);

        filterChain.doFilter(request,response);
    }

    private String getToken(HttpServletRequest request) {
        if (hasToken(request)) {
            return request.getHeader("Authorization");
        } else {
            return null;
        }
    }

    private boolean hasToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return authHeader != null && authHeader.startsWith("Bearer ");
    }

    private static boolean isAuthEndpoint(HttpServletRequest request) {
        return request.getRequestURI().contains("/api/auth");
    }
}
