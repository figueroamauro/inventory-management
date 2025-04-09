package ar.com.old.ms_users.security;

import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.exceptions.UserNotFoundException;
import ar.com.old.ms_users.repositories.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    public CustomUserDetailsService(UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = repository.findByUserNameAndEnabledTrue(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        return new CustomUserDetails(user);
    }
}
