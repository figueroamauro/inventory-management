package ar.com.old.ms_users.services;

import ar.com.old.ms_users.Role;
import ar.com.old.ms_users.dto.UserRequestDTO;
import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.exceptions.UserNotFoundException;
import ar.com.old.ms_users.mappers.UserMapper;
import ar.com.old.ms_users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    @Autowired
    private final UserMapper mapper;

    public UserServiceImpl(UserRepository userRepository, UserMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        validateNull(pageable,"Pageable can not be null");
        return userRepository.findAllByEnabledTrue(pageable);
    }


    @Override
    public User findOne(Long id) {
        validateNull(id,"Id can not be null");
        Optional<User> user = userRepository.findByIdAndEnabledTrue(id);
        if (user.isPresent()) {
            return user.get();
        } else {
            throw new UserNotFoundException("User not found");
        }
    }

    @Override
    public User create(UserRequestDTO dto) {
        User user = mapper.toEntity(dto);
        user.setId(null);
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    @Override
    public User update(UserRequestDTO dto) {
        if (dto.id() == null) {
            throw new IllegalArgumentException("Id can not be null");
        }
        User user = mapper.toEntity(dto);

        return userRepository.save(user);
    }

    @Override
    public void delete(Long id) {

    }


    private static void validateNull(Object obj, String errorMessage) {
        if (obj == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
