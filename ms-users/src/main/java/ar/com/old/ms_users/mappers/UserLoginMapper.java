package ar.com.old.ms_users.mappers;

import ar.com.old.ms_users.dto.UserLoginDTO;
import ar.com.old.ms_users.entities.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserLoginMapper {

    User toEntity(UserLoginDTO dto);
}
