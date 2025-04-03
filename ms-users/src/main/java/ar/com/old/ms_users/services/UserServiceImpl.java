package ar.com.old.ms_users.services;

import ar.com.old.ms_users.dto.UserRequestDTO;
import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    public UserServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Override
    public User findOne(Long id) {
        return null;
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
}
