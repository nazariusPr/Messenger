package com.example.Server.service.impl;

import static com.example.Server.util.GeneralUtil.buildPageDto;

import com.example.Server.dto.general.PageDto;
import com.example.Server.dto.user.UserDto;
import com.example.Server.mapper.UserMapper;
import com.example.Server.model.User;
import com.example.Server.repository.UserRepository;
import com.example.Server.service.UserService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService {
  private final UserRepository repository;
  private final UserMapper mapper;

  @Override
  public User create(UserDto userDto) {
    User user = mapper.dtoToEntity(userDto);
    return repository.save(user);
  }

  @Override
  public User create(String email) {
    UserDto userDto = new UserDto();
    userDto.setEmail(email);

    return create(userDto);
  }

  @Override
  public User findByEmail(String email) {
    return repository.findByEmail(email).orElseThrow(EntityNotFoundException::new);
  }

  @Override
  public boolean doesUserExist(String email) {
    return repository.existsByEmail(email);
  }

  @Override
  public PageDto<UserDto> search(String email, Pageable pageable) {
    String trimmedEmail = (email != null) ? email.trim() : "";

    Page<User> users = repository.search(trimmedEmail, pageable);
    return buildPageDto(users, mapper::entityToDto);
  }
}
