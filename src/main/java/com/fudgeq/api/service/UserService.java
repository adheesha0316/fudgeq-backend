package com.fudgeq.api.service;

import com.fudgeq.api.dto.LoginRequestDto;
import com.fudgeq.api.dto.LoginResponseDto;
import com.fudgeq.api.dto.UserDto;
import com.fudgeq.api.dto.UserDtoReturn;
import com.fudgeq.api.entity.User;
import com.fudgeq.api.enums.Role;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface UserService {
    UserDtoReturn registerUser(UserDto userDto);
    LoginResponseDto loginUser(LoginRequestDto loginRequestDto);
    Optional<UserDtoReturn> getUserById(String id);
    Page<UserDtoReturn> getAllUsers(int page, int size);

    UserDtoReturn approveUser(String userId);
    UserDtoReturn disapproveUser(String userId);
    UserDtoReturn changeUserRole(String userId, Role role);

    User getUserEntityByEmail(String email);
    User getCurrentUserEntity();
    User getUserEntityById(String userId);
    boolean existsByEmail(String email);
    User saveUser(User user);
}
