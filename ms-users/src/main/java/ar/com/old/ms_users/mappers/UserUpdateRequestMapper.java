package ar.com.old.ms_users.mappers;

import ar.com.old.ms_users.dto.UserUpdateRequestDTO;
import ar.com.old.ms_users.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserUpdateRequestMapper {
    UserUpdateRequestDTO toDto(User user);

    User toEntity(UserUpdateRequestDTO dto);
}
