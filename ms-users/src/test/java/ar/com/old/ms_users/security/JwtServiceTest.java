package ar.com.old.ms_users.security;

import ar.com.old.ms_users.entities.User;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.crypto.SecretKey;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private static final long EXPIRATION_TIME = 36000000;

    @Autowired
    private JwtService jwtService;

    @Test
    void shouldGenerateToken() {
        //GIVEN
        User user = new User(1L, "test", "pass1234", "test@mail.com");
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
        User user = new User(1L, "username", "pass1234", "test@mail.com");
        CustomUserDetails userDetails = new CustomUserDetails(user);
        String tokenMock = jwtService.generateToken(userDetails, EXPIRATION_TIME, SECRET_KEY);

        //WHEN
        String result = jwtService.getSubject(tokenMock, SECRET_KEY);

        //THEN
        assertNotNull(result);
        assertEquals("username", result);

    }
}