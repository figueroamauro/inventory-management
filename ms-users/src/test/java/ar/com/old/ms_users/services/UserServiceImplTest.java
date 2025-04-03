package ar.com.old.ms_users.services;

import ar.com.old.ms_users.entities.User;
import ar.com.old.ms_users.repositories.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldReturnPageWithThreeElements(){
        //GIVEN
        List<User> userList = List.of(new User(), new User(), new User());
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> page = new PageImpl<>(userList, pageable, userList.size());
        when(userRepository.findAll(pageable)).thenReturn(page);

        //WHEN
        Page<User> result = userService.findAll(pageable);

        //THEN
        assertNotNull(result);
        assertEquals(3, result.getTotalElements());

        verify(userRepository).findAll(pageable);
    }
}