package ar.com.old.ms_users.dto;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.Test;


import java.util.Set;

import static jakarta.validation.Validation.buildDefaultValidatorFactory;
import static org.junit.jupiter.api.Assertions.*;

class UserRequestDTOTest {
    
    @Test
    void shouldCreateDto_whenAllFieldsAreValid(){
        //GIVEN
        ValidatorFactory validatorFactory = buildDefaultValidatorFactory();
        Validator validator = validatorFactory.getValidator();

        UserRequestDTO dto = new UserRequestDTO(1L, "test", "pass123", "test@mail.com");

        //WHEN
        Set<ConstraintViolation<UserRequestDTO>> errors = validator.validate(dto);

        //THEN
        assertTrue(errors.isEmpty());
    }

}