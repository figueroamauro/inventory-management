package ar.com.old.ms_users.services;

import ar.com.old.ms_users.Role;
import ar.com.old.ms_users.dto.UserRequestDTO;
import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.exceptions.UserNotFoundException;
import ar.com.old.ms_users.mappers.UserMapper;
import ar.com.old.ms_users.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.function.Executable;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    private User user;
    private User savedUser;
    private UserRequestDTO dto;
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper mapper;


    @BeforeEach
    void init() {
        user = new User(null, "test","123123", "test@mail.com");
        savedUser =  new User(1L, "test","123123", "test@mail.com");
        dto = new UserRequestDTO(null, "test", "123123", "test@mail.com");
    }

    @Test
    void shouldReturnPageWithThreeElements() {
        //GIVEN
        List<User> userList = List.of(new User(), new User(), new User());
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(userList, pageable, userList.size());
        when(userRepository.findAllByEnabledTrue(pageable)).thenReturn(page);

        //WHEN
        Page<User> result = userService.findAll(pageable);

        //THEN
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());

        verify(userRepository).findAllByEnabledTrue(pageable);
    }

    @Test
    void shouldThrowException_whenPageableIsNull() {
        //WHEN
        Executable executable = () -> userService.findAll(null);

        //THEN
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("Pageable can not be null", e.getMessage());

        verify(userRepository, never()).findAllByEnabledTrue(any(Pageable.class));
    }

    @Test
    void shouldReturnUserById() {
        //GIVEN
        when(userRepository.findByIdAndEnabledTrue(1L)).thenReturn(Optional.of(savedUser));

        //WHEN
        User result = userService.findOne(1L);

        //THEN
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("test", result.getUserName());

        verify(userRepository).findByIdAndEnabledTrue(1L);
    }

    @Test
    void shouldThrowExceptionFindingById_whenUserNotFound() {
        //WHEN
        Executable executable = () -> userService.findOne(2L);

        //THEN
        UserNotFoundException e = assertThrows(UserNotFoundException.class, executable);
        assertEquals("User not found", e.getMessage());

        verify(userRepository).findByIdAndEnabledTrue(2L);
    }

    @Test
    void shouldThrowExceptionFindingById_whenIdIsNull() {
        //WHEN
        Executable executable = () -> userService.findOne(null);

        //THEN
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, executable);
        assertEquals("Id can not be null", e.getMessage());

        verify(userRepository, never()).findByIdAndEnabledTrue(null);
    }

    @Test
    void shouldCreateAndReturnAnUser() {
        //GIVEN
        when(mapper.toEntity(dto)).thenReturn(user);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        //WHEN
        User result = userService.create(dto);

        //THEN
        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals("test", result.getUserName());

        verify(userRepository).save(any(User.class));
    }
    
    @Test
    void shouldSetIdNullAndDefaultRole_beforeToSave(){
        //GIVEN
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        when(mapper.toEntity(dto)).thenReturn(user);

        //WHEN
        userService.create(dto);

        //THEN
        verify(userRepository).save(captor.capture());
        User capture = captor.getValue();

        assertNull(capture.getId());
        assertEquals("USER", capture.getRole().name());
    }

    @Test
    void shouldUpdateUser(){
        //GIVEN
        savedUser.setRole(Role.USER);
        when(mapper.toEntity(dto)).thenReturn(savedUser);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        //WHEN
        User result = userService.update(dto);

        //THEN
        assertEquals(savedUser.getId(), result.getId());
        assertEquals(savedUser.getUserName(), result.getUserName());
        assertEquals(savedUser.getEmail(), result.getEmail());
    }
}