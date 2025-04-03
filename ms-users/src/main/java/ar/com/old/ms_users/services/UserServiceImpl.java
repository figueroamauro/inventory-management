package ar.com.old.ms_users.services;

import ar.com.old.ms_users.dto.UserRequestDTO;
import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.exceptions.UserNotFoundException;
import ar.com.old.ms_users.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        validateNull(pageable,"Pageable can not be null");
        return userRepository.findAllByEnabledTrue(pageable);
    }


    @Override
    public User findOne(Long id) {
        Optional<User> user = userRepository.findByIdAndEnabledTrue(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    @Override
    public User create(UserRequestDTO dto) {
        return null;
    }

    @Override
    public User update(UserRequestDTO dto) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }


    private static void validateNull(Object o, String message) {
        if (o == null) {
            throw new IllegalArgumentException(message);
        }
    }
}
