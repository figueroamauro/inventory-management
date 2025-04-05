package ar.com.old.ms_users.mappers;

import ar.com.old.ms_users.dto.UserResponseDTO;
import ar.com.old.ms_users.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserResponseMapper {
    UserResponseDTO toDto(User user);

    User toEntity(UserResponseDTO dto);
}
