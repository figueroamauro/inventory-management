package ar.com.old.ms_users.services;

import ar.com.old.ms_users.dto.UserUpdateRequestDTO;
import ar.com.old.ms_users.enumerations.Role;
import ar.com.old.ms_users.dto.UserRequestDTO;
import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.exceptions.ChangeUserNameException;
import ar.com.old.ms_users.exceptions.UserAlreadyExistException;
import ar.com.old.ms_users.exceptions.UserNotFoundException;
import ar.com.old.ms_users.mappers.UserRequestMapper;
import ar.com.old.ms_users.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    @Override
    public User create(UserRequestDTO dto) {
        validateNull(dto,"DTO can not be null");
        validateDuplicatedUserNameOrEmail(dto);
        User user = mapper.toEntity(dto);
        user.setId(null);
        user.setRole(Role.USER);

        return userRepository.save(user);
    }

    @Override
    public User update(UserUpdateRequestDTO dto) {
        validateNull(dto,"DTO can not be null");
        User user = findUser(dto);

        validateDuplicatedUserName(dto, user);
        validateDuplicatedEmail(dto, user);

        mapToUser(dto, user);

        return userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        validateNull(id, "Id can not be null");
        userRepository.findByIdAndEnabledTrue(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        userRepository.deleteLogicById(id);
    }


    private  void validateNull(Object obj, String errorMessage) {
        if (obj == null) {
            throw new IllegalArgumentException(errorMessage);
        }
    }

    private void validateDuplicatedUserNameOrEmail(UserRequestDTO dto) {
        Optional<User> foundUser = userRepository.findByEmailOrUserNameAndEnabledTrue(dto.email(), dto.userName());
        if (foundUser.isPresent()) {
            throw new UserAlreadyExistException("Username or email already exist");
        }
    }

    private User findUser(UserUpdateRequestDTO dto) {
        return userRepository.findByIdAndEnabledTrue(dto.id())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    private  void validateDuplicatedUserName(UserUpdateRequestDTO dto, User user) {
        if (!user.getUserName().equals(dto.userName())) {
            throw new ChangeUserNameException("Username can not be changed");
        }
    }

    private void validateDuplicatedEmail(UserUpdateRequestDTO dto, User user) {
        Optional<User> userFound = userRepository.findByEmailAndEnabledTrue(dto.email());
        if (userFound.isPresent()) {
            if (!userFound.get().getEmail().equals(user.getEmail())) {
                throw new UserAlreadyExistException("Email already exist");
            }
        }
    }

    private void mapToUser(UserUpdateRequestDTO dto, User user) {
        mapper.updateUserFromDto(dto, user);
    }

}
