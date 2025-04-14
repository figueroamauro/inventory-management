package ar.com.old.ms_users.security;

import ar.com.old.ms_users.entities.User;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 36000000; //1h

    @Autowired
    private JwtService jwtService;
    private User user;
    private CustomUserDetails userDetails;

    @BeforeEach
    void init() {
        user = new User(1L, "username", "pass1234", "test@mail.com");
        userDetails = new CustomUserDetails(user);
    }

    @Test
    void shouldGenerateToken() {
        //GIVEN
        CustomUserDetails userDetails = new CustomUserDetails(user);

        //WHEN
        String token = jwtService.generateToken(userDetails);

        //THEN
        assertNotNull(token);
        assertTrue(token.length() > 50);
    }

    @Test
    void shouldObtainUserName() {
        //GIVEN
        String tokenMock = jwtService.generateToken(userDetails, EXPIRATION_TIME, SECRET_KEY);

        //WHEN
        String result = jwtService.getSubject(tokenMock, SECRET_KEY);

        //THEN
        assertNotNull(result);
        assertEquals("username", result);
    }

    @Test
    void shouldVerifyExpiredToken(){
        //GIVEN
        String expiredToken = jwtService.generateToken(userDetails, -1L, SECRET_KEY);
        String validToken = jwtService.generateToken(userDetails);

        //WHEN
        boolean valid = jwtService.isExpired(validToken);
        Executable executable = ()-> jwtService.isExpired(expiredToken, SECRET_KEY);

        //THEN
        JwtException e = assertThrows(JwtException.class, executable);
        assertEquals("Expired token", e.getMessage());
        assertFalse(valid);
    }

    @Test
    void shouldVerifyValidToken(){
        //GIVEN
        String validToken = jwtService.generateToken(userDetails);

        //WHEN
        boolean valid = jwtService.isValid(validToken, userDetails);

        //THEN
        assertTrue(valid);
    }

    @Test
    void shouldThrowException_whenTokenIsExpired() {
        //GIVEN
        String tokenMock = jwtService.generateToken(userDetails, -1L, SECRET_KEY);

        //WHEN
        Executable executable = () -> jwtService.getSubject(tokenMock, SECRET_KEY);

        //THEN
        JwtException e = assertThrows(JwtException.class, executable);
        assertEquals("Expired token", e.getMessage());
    }

    @Test
    void shouldThrowException_whenTokenHasInvalidSign() {
        //GIVEN
        String tokenMock = jwtService.generateToken(userDetails, EXPIRATION_TIME, SECRET_KEY);

        //WHEN
        Executable executable = () -> jwtService.getSubject(tokenMock,
                Keys.secretKeyFor(SignatureAlgorithm.HS256) );

        //THEN
        JwtException e = assertThrows(JwtException.class, executable);
        assertEquals("Invalid token signature", e.getMessage());
    }

    @Test
    void shouldThrowException_whenTokenHasInvalidToken() {
        //GIVEN
        String invalidToken = "invalid token";

        //WHEN
        Executable executable = () -> jwtService.getSubject(invalidToken,SECRET_KEY);

        //THEN
        JwtException e = assertThrows(JwtException.class, executable);
        assertEquals("Invalid token", e.getMessage());
    }


}