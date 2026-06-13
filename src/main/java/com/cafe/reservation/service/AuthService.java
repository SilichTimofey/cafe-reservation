package com.cafe.reservation.service;

import com.cafe.reservation.dto.auth.AuthResponse;
import com.cafe.reservation.dto.auth.LoginRequest;
import com.cafe.reservation.model.Role;
import com.cafe.reservation.model.User;
import com.cafe.reservation.repository.UserRepository;
import com.cafe.reservation.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByPhoneNumber(request.phoneNumber())
                .orElseGet(() -> userRepository.save(User.builder()
                        .name(request.name())
                        .phoneNumber(request.phoneNumber())
                        .role(Role.USER)
                        .build()));

        String role = "ROLE_" + user.getRole().name();
        String token = jwtService.generateToken(user.getId(), user.getPhoneNumber(), role);
        return new AuthResponse(token, "Bearer", user.getId(), role);
    }
}
