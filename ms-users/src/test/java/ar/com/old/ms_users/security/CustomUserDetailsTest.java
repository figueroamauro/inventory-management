package ar.com.old.ms_users.security;

import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.enumerations.Role;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CustomUserDetailsTest {

    @Test
    void shouldAssignDefaultValues_whenIsCreated(){
        //GIVEN
        User user = new User(1L, "username", "pass1234", "user@mail.com");
        user.setRole(Role.USER);

        //WHEN
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        //THEN
        assertEquals(user.getUserName(), customUserDetails.getUsername());
        assertEquals(user.getPassword(), customUserDetails.getPassword());
        assertTrue(customUserDetails.isAccountNonExpired());
        assertTrue(customUserDetails.isEnabled());
        assertTrue(customUserDetails.isCredentialsNonExpired());
        assertTrue(customUserDetails.isAccountNonLocked());
        assertEquals("ROLE_USER", customUserDetails.getAuthorities().iterator().next().getAuthority());

    }

}