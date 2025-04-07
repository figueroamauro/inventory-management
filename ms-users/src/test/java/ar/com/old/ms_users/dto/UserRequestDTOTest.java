package ar.com.old.ms_users.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;


import java.util.Set;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

class UserRequestDTOTest {
    private Validator validator;
    private UserRequestDTO dto;
    private Set<ConstraintViolation<UserRequestDTO>> errors;

    @BeforeEach
    void init() {
        ValidatorFactory validatorFactory = buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();


    }

    @Test
    void shouldCreateDto_whenAllFieldsAreValid() {
        //GIVEN
        dto = new UserRequestDTO(1L, "test", "pass123", "test@mail.com");

        //WHEN
        errors = validator.validate(dto);

        //THEN
        assertTrue(errors.isEmpty());
    }

    @ParameterizedTest
    @NullAndEmptySource
    void shouldThrowException_whenUserNameIsBlank(String username) {
        //GIVEN
        dto = new UserRequestDTO(1L, username, "pass123", "test@mail.com");

        //WHEN
        errors = validator.validate(dto);

        //THEN
        assertTrue(errors.stream().anyMatch(e -> e.getMessage().equals("Username can not be blank")));
    }

}