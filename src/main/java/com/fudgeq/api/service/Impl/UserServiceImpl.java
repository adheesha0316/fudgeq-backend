package com.fudgeq.api.service.Impl;

import com.fudgeq.api.dto.LoginRequestDto;
import com.fudgeq.api.dto.LoginResponseDto;
import com.fudgeq.api.dto.UserDto;
import com.fudgeq.api.dto.UserDtoReturn;
import com.fudgeq.api.entity.Profile;
import com.fudgeq.api.entity.User;
import com.fudgeq.api.enums.AuthProvider;
import com.fudgeq.api.enums.Role;
import com.fudgeq.api.enums.UserStatus;
import com.fudgeq.api.repo.UserRepo;
import com.fudgeq.api.service.UserService;
import com.fudgeq.api.utill.AppConstants;
import com.fudgeq.api.utill.CustomIdGenerator;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepo userRepo;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper mapper;
    private final CustomIdGenerator idGenerator;

    @Override
    @Transactional
    public UserDtoReturn registerUser(UserDto userDto) {
        if (userRepo.existsByEmail(userDto.getEmail())) {
            throw new RuntimeException("Email already exists: " + userDto.getEmail());
        }

        // Generate IDs
        String userId = idGenerator.generateNextId(AppConstants.PREFIX_USER);
        String profileId = idGenerator.generateNextId("FQ-PRF-");

        // Set default role if null
        Role role = (userDto.getRole() != null) ? userDto.getRole() : Role.CUSTOMER;

        User user = User.builder()
                .userId(userId)
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .role(role)
                .authProvider(AuthProvider.LOCAL)
                .isActive(true)
                .build();

        // One-to-One: Linking profile
        Profile profile = Profile.builder()
                .profileId(profileId)
                .user(user)
                .build();
        user.setProfile(profile);

        // PrePersist handles status logic (ADMIN/MODERATOR -> PENDING)
        User savedUser = userRepo.save(user);
        return mapper.map(savedUser, UserDtoReturn.class);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDto loginUser(LoginRequestDto loginRequestDto) {
        User user = getUserEntityByEmail(loginRequestDto.getEmail());

        if (!passwordEncoder.matches(loginRequestDto.getPassword(), user.getPassword())) {
            throw new BadCredentialsException("Invalid credentials");
        }

        return LoginResponseDto.builder()
                .userId(user.getUserId())
                .email(user.getEmail())
                .role(user.getRole())
                .firstName(user.getFirstName())
                .status(user.getStatus())
                .build();
    }

    @Override

    public Optional<UserDtoReturn> getUserById(String id) {
        return userRepo.findById(id).map(user -> mapper.map(user, UserDtoReturn.class));
    }

    @Override
    public Page<UserDtoReturn> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return userRepo.findAll(pageable).map(user -> mapper.map(user, UserDtoReturn.class));
    }

    @Override
    @Transactional
    public UserDtoReturn approveUser(String userId) {
        User user = getUserEntityById(userId);
        user.setStatus(UserStatus.APPROVED);
        return mapper.map(userRepo.save(user), UserDtoReturn.class);
    }

    @Override
    @Transactional
    public UserDtoReturn disapproveUser(String userId) {
        User user = getUserEntityById(userId);
        user.setStatus(UserStatus.REJECTED);
        return mapper.map(userRepo.save(user), UserDtoReturn.class);
    }

    @Override
    @Transactional
    public UserDtoReturn changeUserRole(String userId, Role role) {
        User user = getUserEntityById(userId);
        user.setRole(role);
        return mapper.map(userRepo.save(user), UserDtoReturn.class);
    }

    @Override
    public User getUserEntityByEmail(String email) {
        return userRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found: " + email));
    }

    @Override
    public User getCurrentUserEntity() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return getUserEntityByEmail(email);
    }

    @Override
    public User getUserEntityById(String userId) {
        return userRepo.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found ID: " + userId));
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }

    @Override
    public User saveUser(User user) {
        return userRepo.save(user);
    }
}
