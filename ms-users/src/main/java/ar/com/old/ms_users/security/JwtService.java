package ar.com.old.ms_users.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
public class JwtService {
    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 3600000;

    public String generateToken(CustomUserDetails userDetails, long expiration, SecretKey key) {
        return Jwts.builder()
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key)
                .compact();
    }

    public String generateToken(CustomUserDetails userDetails) {
        return generateToken(userDetails, EXPIRATION_TIME, SECRET_KEY);
    }

    public String getSubject(String token, SecretKey key) {
        return buildClaims(token, key).getSubject();
    }

    public boolean isExpired(String token, SecretKey key) {
        return buildClaims(token, key).getExpiration().before(new Date());
    }

    public boolean isExpired(String token) {
        return isExpired(token, SECRET_KEY);
    }

    public boolean isValid(String validToken, CustomUserDetails userDetails) {
        return !isExpired(validToken) && getSubject(validToken).equals(userDetails.getUsername());
    }


    private static Claims buildClaims(String token, SecretKey key) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

        } catch (ExpiredJwtException e) {
            throw new JwtException("Expired token", e);
        } catch (SignatureException e) {
            throw new JwtException("Invalid token signature", e);
        } catch (JwtException e) {
            throw new JwtException("Invalid token", e);
        }
    }

    public String getSubject(String token) {
        return getSubject(token, SECRET_KEY);
    }



}
