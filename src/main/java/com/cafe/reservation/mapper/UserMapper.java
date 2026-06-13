package com.cafe.reservation.mapper;

import com.cafe.reservation.dto.UserRequestDTO;
import com.cafe.reservation.dto.UserResponseDTO;
import com.cafe.reservation.model.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    public User toEntity(UserRequestDTO dto) {
        return User.builder()
                .name(dto.name())
                .phoneNumber(dto.phoneNumber())
                .role(dto.role())
                .build();
    }

    public void update(User user, UserRequestDTO dto) {
        user.setName(dto.name());
        user.setPhoneNumber(dto.phoneNumber());
        user.setRole(dto.role());
    }

    public UserResponseDTO toResponse(User u) {
        return new UserResponseDTO(u.getId(), u.getName(), u.getPhoneNumber(), u.getRole());
    }
}
