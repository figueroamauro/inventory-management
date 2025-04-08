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


import java.util.List;
import java.util.Set;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

class UserRequestDTOTest {
    private static final String TOO_LONG_20 = "AAAAABBBBBCCCCCDDDDDE";
    private static final String TOO_LONG_30 = "AAAAABBBBBCCCCCDDDDDEEEEEFFFFFG";
    private static final String CORRECT_PASS = "pass1234";
    private static final String CORRECT_EMAIL = "test@mail.com";
    private static final String CORRECT_USERNAME = "test_01";

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
        @ValueSource(strings = {"joh", TOO_LONG_20})
        void shouldThrowException_whenUserNameIsShorterThan4OrLongerThan20(String username) {
            //GIVEN
            dto = new UserRequestDTO(1L, username, CORRECT_PASS, CORRECT_EMAIL);

            //WHEN
            validateDTO(dto);

            //THEN
            assertErrors("Username must be between 4 and 20 characters long");
        }

        @ParameterizedTest
        @ValueSource(strings = {"j oh", "  t@est", "john doe", "johndoe ","|@#~½~#¬ŧ"})
        void shouldThrowException_whenUserNameHasInvalidPattern(String username) {
            //GIVEN
            dto = new UserRequestDTO(1L, username, CORRECT_PASS, CORRECT_EMAIL);

            //WHEN
            validateDTO(dto);

            //THEN
            assertErrors("Invalid username pattern. Must contain only alphanumeric characters and underscores");
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

        @ParameterizedTest
        @ValueSource(strings = {"pas", TOO_LONG_30})
        void shouldThrowException_whenPasswordIsShorterThan8OrLongerThan30(String password) {
            //GIVEN
            dto = new UserRequestDTO(1L, CORRECT_USERNAME, password, CORRECT_EMAIL);

            //WHEN
            validateDTO(dto);

            //THEN
            assertErrors("Password must be between 8 and 30 characters long");
        }

        @ParameterizedTest
        @ValueSource(strings = {"pass;123", " pass123", "..pass@.com","","   ", "1.aaaPASS;"})
        void shouldThrowException_whenPasswordHasInvalidPattern(String password) {
            //GIVEN
            dto = new UserRequestDTO(1L, CORRECT_USERNAME, password, CORRECT_EMAIL);

            //WHEN
            validateDTO(dto);

            //THEN
            assertErrors("Invalid password pattern. Must contain 1 number, 1 letter and without whitespaces");
        }
    }

    private void assertErrors(String message) {
        List<String> list = errors.stream().map(ConstraintViolation::getMessage).toList();
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals(message)), list.toString());
    }

    private void validateDTO(UserRequestDTO dto) {
        errors = validator.validate(dto);
    }
}
