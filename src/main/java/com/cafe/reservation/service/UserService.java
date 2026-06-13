package com.cafe.reservation.service;

import com.cafe.reservation.dto.UserRequestDTO;
import com.cafe.reservation.dto.UserResponseDTO;
import com.cafe.reservation.exception.DuplicateResourceException;
import com.cafe.reservation.exception.ResourceNotFoundException;
import com.cafe.reservation.mapper.UserMapper;
import com.cafe.reservation.model.User;
import com.cafe.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository repository;
    private final UserMapper mapper;

    public List<UserResponseDTO> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public UserResponseDTO findById(Long id) {
        return mapper.toResponse(get(id));
    }

    @Transactional
    public UserResponseDTO create(UserRequestDTO dto) {
        if (repository.existsByPhoneNumber(dto.phoneNumber())) {
            throw new DuplicateResourceException("Phone number already registered: " + dto.phoneNumber());
        }
        return mapper.toResponse(repository.save(mapper.toEntity(dto)));
    }

    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User user = get(id);
        mapper.update(user, dto);
        return mapper.toResponse(user);
    }

    @Transactional
    public void delete(Long id) {
        repository.delete(get(id));
    }

    private User get(Long id) {
        return repository.findById(id).orElseThrow(() -> ResourceNotFoundException.of("User", id));
    }
}
