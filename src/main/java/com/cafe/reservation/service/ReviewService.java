package com.cafe.reservation.service;

import com.cafe.reservation.dto.ReviewRequestDTO;
import com.cafe.reservation.dto.ReviewResponseDTO;
import com.cafe.reservation.exception.ResourceNotFoundException;
import com.cafe.reservation.mapper.ReviewMapper;
import com.cafe.reservation.model.Review;
import com.cafe.reservation.model.User;
import com.cafe.reservation.repository.ReviewRepository;
import com.cafe.reservation.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository repository;
    private final UserRepository userRepository;
    private final ReviewMapper mapper;

    public List<ReviewResponseDTO> findAll() {
        return repository.findAll().stream().map(mapper::toResponse).toList();
    }

    public List<ReviewResponseDTO> findByUser(Long userId) {
        return repository.findByUserId(userId).stream().map(mapper::toResponse).toList();
    }

    @Transactional
    public ReviewResponseDTO create(ReviewRequestDTO dto, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> ResourceNotFoundException.of("User", userId));
        Review review = Review.builder()
                .user(user)
                .rating(dto.rating())
                .comment(dto.comment())
                .build();
        return mapper.toResponse(repository.save(review));
    }
}
