package com.example.Server.service;

import com.example.Server.dto.general.PageDto;
import com.example.Server.dto.user.UserDto;
import com.example.Server.model.User;
import org.springframework.data.domain.Pageable;

public interface UserService {
  User create(UserDto userDto);

  User create(String email);

  User findByEmail(String email);

  boolean doesUserExist(String email);

  PageDto<UserDto> search(String email, Pageable pageable);
}
