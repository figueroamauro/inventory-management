package ar.com.old.ms_users.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;


import java.util.Set;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

class UserRequestDTOTest {
    private static final String NAME_TOO_LONG_20 = "AAAAABBBBBCCCCCDDDDDE";
    private static final String CORRECT_PASS = "pass123";
    private static final String CORRECT_EMAIL = "test@mail.com";
    private static final String CORRECT_USERNAME = "test";

    private Validator validator;
    private UserRequestDTO dto;
    private Set<ConstraintViolation<UserRequestDTO>> errors;

    @BeforeEach
    void init() {
        ValidatorFactory validatorFactory = buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
        errors = null;
    }

    @Test
    void shouldCreateDto_whenAllFieldsAreValid() {
        //GIVEN
        dto = new UserRequestDTO(1L, CORRECT_USERNAME, CORRECT_PASS, CORRECT_EMAIL);

        //WHEN
        validateDTO(dto);

        //THEN
        assertTrue(errors.isEmpty());
    }

    @Nested
    class UserNameTest {

        @ParameterizedTest
        @NullAndEmptySource
        void shouldThrowException_whenUserNameIsBlank(String username) {
            //GIVEN
            dto = new UserRequestDTO(1L, username, CORRECT_PASS, CORRECT_EMAIL);

            //WHEN
            validateDTO(dto);

            //THEN
            assertErrors("Username can not be blank");

        }


        @ParameterizedTest
        @ValueSource(strings = {"joh", NAME_TOO_LONG_20})
        void shouldThrowException_whenUserNameTooLess4AndTooLong20(String username) {
            //GIVEN
            dto = new UserRequestDTO(1L, username, CORRECT_PASS, CORRECT_EMAIL);

            //WHEN
            validateDTO(dto);

            //THEN
            assertErrors("Username must be between 4 and 20 characters long");
        }

        @ParameterizedTest
        @ValueSource(strings = {"j oh", "  test", "john doe", "johndoe "})
        void shouldThrowException_whenUserNameContainsWhiteSpaces(String username) {
            //GIVEN
            dto = new UserRequestDTO(1L, username, CORRECT_PASS, CORRECT_EMAIL);

            //WHEN
            validateDTO(dto);

            //THEN
            assertErrors("Username must not contain spaces");
        }
    }

    @Nested
    class PasswordTest {

        @ParameterizedTest
        @NullAndEmptySource
        void shouldThrowException_whenEmailIsBlank(String password) {
            //GIVEN
            dto = new UserRequestDTO(1L, CORRECT_USERNAME, password, CORRECT_EMAIL);

            //WHEN
            validateDTO(dto);

            //THEN
            assertErrors("Password can not be blank");
        }
    }

    private void assertErrors(String message) {
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(message)), errors.toString());
    }

    private void validateDTO(UserRequestDTO dto) {
        errors = validator.validate(dto);
    }
}
