package com.example.Server.mapper;

import com.example.Server.dto.user.UserDto;
import com.example.Server.model.User;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserMapper {
  User dtoToEntity(UserDto dto);

  UserDto entityToDto(User user);
}
