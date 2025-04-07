package ar.com.old.ms_users.services;

import ar.com.old.ms_users.enumerations.Role;
import ar.com.old.ms_users.dto.UserRequestDTO;
import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.exceptions.UserNotFoundException;
import ar.com.old.ms_users.mappers.UserRequestMapper;
import ar.com.old.ms_users.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;


@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserRequestMapper mapper;

    public UserServiceImpl(UserRepository userRepository, UserRequestMapper mapper) {
        this.userRepository = userRepository;
        this.mapper = mapper;
    }

    @Override
    public Page<User> findAll(Pageable pageable) {
        validateNull(pageable, "Pageable can not be null");
        return userRepository.findAllByEnabledTrue(pageable);
    }


    @Override
    public User findOne(Long id) {
        validateNull(id, "Id can not be null");

        return userRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(()-> new UserNotFoundException("User not found"));
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
        requireIdValidation(dto.id());
        User user = userRepository.findByIdAndEnabledTrue(dto.id())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Role role = user.getRole();
        user = mapper.toEntity(dto);
        user.setRole(role);

        return userRepository.save(user);

    }


    @Override
    public void delete(Long id) {
        validateNull(id, "Id can not be null");
        userRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(()-> new UserNotFoundException("User not found"));

        userRepository.deleteLogicById(id);

    }


    private static void validateNull(Object obj, String errorMessage) {
        if (obj == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private static void requireIdValidation(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("Id can not be null");
        }
    }
}
