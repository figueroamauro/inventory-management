package ar.com.old.ms_users.security;

import ar.com.old.ms_users.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ActiveProfiles("test")
class JwtServiceTest {

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
}