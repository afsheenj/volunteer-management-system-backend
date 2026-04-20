package com.vserve.project.service.impl;

import com.vserve.project.dto.admin.AdminProfileDto;
import com.vserve.project.dto.admin.AdminUserResponseDto;
import com.vserve.project.dto.admin.CreateAdminDto;
import com.vserve.project.dto.user.UserRegisterRequestDto;
import com.vserve.project.entity.User;
import com.vserve.project.enums.AccountStatus;
import com.vserve.project.enums.Role;
import com.vserve.project.exception.BusinessException;
import com.vserve.project.repository.UserAddressRepository;
import com.vserve.project.repository.UserRepository;
import com.vserve.project.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserAddressRepository userAddressRepository;
    private final JavaMailSender javaMailSender;



    public UserServiceImpl(UserRepository userRepository, UserAddressRepository userAddressRepository, JavaMailSender javaMailSender) {
        this.userRepository = userRepository;
        this.userAddressRepository=userAddressRepository;
        this.javaMailSender = javaMailSender;
    }
    PasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);

    @Override
    public String registerUser(UserRegisterRequestDto dto) {

        // email check
        User existingUser = userRepository.findByEmail(dto.email());

        if (existingUser != null) {
            throw new BusinessException("Email already registered");
        }

        User existingUserByPhone = userRepository.findByPhone(dto.phone());

        if (existingUserByPhone != null) {
            throw new BusinessException("Phone number already exists");
        }

        // password match check
        if (!dto.password().equals(dto.confirmPassword())) {
            throw new BusinessException("Passwords do not match");
        }

        User user = mapToUsers(dto);
        System.out.println(user);
        userRepository.save(user);
        return "User registered successfully";
    }

    @Override
    public String updateUser(Long id, UserRegisterRequestDto dto) {
        User user=userRepository.findById(id).orElse(null);
        if(user==null){
            throw new ResourceAccessException("User not found");
        }
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPhone(dto.phone());
        if(dto.role()!=null)
            user.setRole(dto.role());
        user.setBio(dto.bio());
        userRepository.save(user);
        return "User details updated Successfully";
    }



    private User mapToUsers(UserRegisterRequestDto dto){
        // create user
        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setPhone(dto.phone());
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setRole(dto.role());
        user.setStatus(AccountStatus.PENDING_VERIFICATION);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        user.setBio(dto.bio());
        return user;
    }

    @Override
    public Page<AdminUserResponseDto> getAllUsers(
            int page,
            int size,
            String username,
            String role,
            Boolean availability
    ) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<User> userPage;

        Role roleEnum = null;
        if (role != null && !role.isBlank()) {
            try {
                roleEnum = Role.valueOf(role.toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new BusinessException("Invalid role");
            }
        }


        if (username != null && !username.isBlank() && roleEnum != null && availability != null) {
            userPage = userRepository.findByUsernameContainingIgnoreCaseAndRoleAndAvailability(username, roleEnum, availability, pageable);

        } else if (username != null && !username.isBlank() && roleEnum != null) {
            userPage = userRepository.findByUsernameContainingIgnoreCaseAndRole(username, roleEnum, pageable);

        } else if (username != null && !username.isBlank()) {
            userPage = userRepository.findByUsernameContainingIgnoreCase(username, pageable);

        } else if (roleEnum != null) {
            userPage = userRepository.findByRole(roleEnum, pageable);

        } else if (availability != null) {
            userPage = userRepository.findByAvailability(availability, pageable);

        } else {
            userPage = userRepository.findAll(pageable);
        }


        return userPage.map(user -> new AdminUserResponseDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.getStatus().name(),
                user.getAvailability(),
                user.getCreatedAt()
        ));
    }

    @Override
    public AdminProfileDto getAdminProfile(Long id) {

        User user = userRepository.findById(id).orElse(null);
        if(user == null)
            throw new BusinessException("User not found");

        return new AdminProfileDto(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPhone()
        );
    }

    private User getCurrentLoggedInUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("User not authenticated");
        }

        String username = authentication.getName();

        User user = userRepository.findByUsername(username);

        if(user == null)
            throw new BusinessException("User not found");

        return user;
    }

    @Override
    public List<AdminProfileDto> getAllAdmins() {

        return userRepository.findByRole(Role.ADMIN)
                .stream()
                .map(u -> new AdminProfileDto(
                        u.getId(),
                        u.getUsername(),
                        u.getEmail(),
                        u.getPhone()
                ))
                .toList();
    }

    @Override
    public void createAdmin(CreateAdminDto dto) {

        User existing = userRepository.findByEmail(dto.email());
        if (existing != null) {
            throw new BusinessException("Email already exists");
        }

        String tempPassword = UUID.randomUUID().toString().substring(0, 8);

        User user = new User();
        user.setUsername(dto.username());
        user.setEmail(dto.email());
        user.setRole(Role.ADMIN);
        user.setPhone(dto.phone());
        user.setPassword(passwordEncoder.encode(tempPassword));

        userRepository.save(user);

        sendAdminCredentials(
                dto.email(),
                dto.username(),
                tempPassword
        );
    }

    public void sendAdminCredentials(String email, String username, String password) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Admin Account Created");

        message.setText(
                "Hello " + username + ",\n\n"
                        + "Your admin account has been created.\n\n"
                        + "Username: " + username + "\n"
                        + "Password: " + password + "\n\n"
                        + "Please login and change your password."
        );

        javaMailSender.send(message);
    }
}
