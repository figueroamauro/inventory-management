package ar.com.old.ms_users.mappers;

import ar.com.old.ms_users.dto.UserRequestDTO;
import ar.com.old.ms_users.dto.UserResponseDTO;
import ar.com.old.ms_users.dto.UserUpdateRequestDTO;
import ar.com.old.ms_users.entities.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserRequestMapper userRequestMapper = Mappers.getMapper(UserRequestMapper.class);
    private final UserResponseMapper userResponseMapper = Mappers.getMapper(UserResponseMapper.class);
    private User user;

    @BeforeEach
    void init() {
        user = new User(1L, "test", "pass1234", "test@mail.com");
    }

    @Nested
    class RequestMapper {

        @Test
        void shouldMapToDTO(){
            //WHEN
            UserRequestDTO result = userRequestMapper.toDto(user);

            //THEN
            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("test", result.userName());
            assertEquals("pass1234", result.password());
            assertEquals("test@mail.com", result.email());
        }

        @Test
        void shouldMapToUser(){
            //GIVEN
            UserRequestDTO userRequestDTO = new UserRequestDTO(1L, "test", "pass1234", "test@mail.com");

            //WHEN
            User result = userRequestMapper.toEntity(userRequestDTO);

            //THEN
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("test", result.getUserName());
            assertEquals("pass1234", result.getPassword());
            assertEquals("test@mail.com", result.getEmail());
        }

        @Test
        void shouldMapToUserUpdating(){
            //GIVEN
            UserUpdateRequestDTO userRequestDTO = new UserUpdateRequestDTO(1L, "newTest", "newEmail@mail.com");

            //WHEN
            User result = new User(1L, "test", "pass1234", "test@mail.com");
            userRequestMapper.updateUserFromDto(userRequestDTO, result);

            //THEN
            assertNotNull(result);
            assertEquals(1L, result.getId());
            assertEquals("newTest", result.getUserName());
            assertEquals("pass1234", result.getPassword());
            assertEquals("newEmail@mail.com", result.getEmail());
        }

        @Test
        void shouldReturnNull_whenInputIsNull(){
            //WHEN
            UserRequestDTO result1 = userRequestMapper.toDto(null);
            User result2 = userRequestMapper.toEntity(null);

            //THEN
            assertNull(result1);
            assertNull(result2);
        }

    }

    @Nested
    class ResponseMapper {

        @Test
        void shouldMapToDto(){
            //WHEN
            UserResponseDTO result = userResponseMapper.toDto(user);

            //THEN
            assertNotNull(result);
            assertEquals(1L, result.id());
            assertEquals("test", result.userName());
            assertEquals("test@mail.com",result.email());
        }

    }

}