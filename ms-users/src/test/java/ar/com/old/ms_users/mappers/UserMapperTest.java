package ar.com.old.ms_users.mappers;

import ar.com.old.ms_users.dto.UserRequestDTO;
import ar.com.old.ms_users.dto.UserUpdateRequestDTO;
import ar.com.old.ms_users.entities.User;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {

    private final UserRequestMapper userRequestMapper = Mappers.getMapper(UserRequestMapper.class);
    private final UserUpdateRequestMapper userUpdateRequestMapper = Mappers.getMapper(UserUpdateRequestMapper.class);

    @Nested
    class RequestMapper {

        @Test
        void shouldMapToDTO(){
            //GIVEN
            User user = new User(1L, "test", "pass1234", "test@mail.com");

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

        @Nested
        class UpdateRequestMapper {

            @Test
            void shouldMapToDTO(){
                //GIVEN
                User user = new User(1L, "test", "pass1234", "test@mail.com");

                //WHEN
                UserUpdateRequestDTO result = userUpdateRequestMapper.toDto(user);

                //THEN
                assertNotNull(result);
                assertEquals(1L, result.id());
                assertEquals("test", result.userName());
                assertEquals("test@mail.com", result.email());
            }

            @Test
            void shouldMapToUser(){
                //GIVEN
                UserUpdateRequestDTO userRequestDTO = new UserUpdateRequestDTO(1L, "test", "test@mail.com");

                //WHEN
                User result = new User(1L, "test", "pass1234", "test@mail.com");
                userUpdateRequestMapper.updateUserFromDto(userRequestDTO, result);

                //THEN
                assertNotNull(result);
                assertEquals(1L, result.getId());
                assertEquals("test", result.getUserName());
                assertEquals("pass1234", result.getPassword());
                assertEquals("test@mail.com", result.getEmail());
            }

        }

    }

}