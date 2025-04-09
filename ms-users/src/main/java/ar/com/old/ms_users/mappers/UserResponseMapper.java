package ar.com.old.ms_users.mappers;

import ar.com.old.ms_users.dto.UserResponseDTO;
import ar.com.old.ms_users.dto.UserUpdateRequestDTO;
import ar.com.old.ms_users.entities.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface UserResponseMapper {
    UserResponseDTO toDto(User user);

    User toEntity(UserResponseDTO dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserUpdateRequestDTO dto, @MappingTarget User user);
}
