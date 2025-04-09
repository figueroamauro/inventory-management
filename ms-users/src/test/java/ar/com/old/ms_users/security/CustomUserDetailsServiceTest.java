package ar.com.old.ms_users.security;

import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService userDetailsService;
    @Mock
    private UserRepository repository;

    @Test
    void shouldGetUserByUserName(){
        //GIVEN
        String userName = "test";
        User user = new User(1L, userName, "pass1234", "test@mail.com");
        when(repository.findByUserNameAndEnabledTrue(userName)).thenReturn(Optional.of(user));

        //WHEN
        UserDetails userDetails = userDetailsService.loadUserByUsername(userName);

        //THEN
        assertNotNull(userDetails);
        assertEquals(userName, userDetails.getUsername());
        assertEquals("pass1234", userDetails.getPassword());
    }

}