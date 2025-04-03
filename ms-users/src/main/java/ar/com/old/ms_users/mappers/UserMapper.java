package ar.com.old.ms_users.mappers;

import ar.com.old.ms_users.dto.UserRequestDTO;
import ar.com.old.ms_users.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserRequestDTO toDto(User user);
    User toEntity(UserRequestDTO dto);
}


