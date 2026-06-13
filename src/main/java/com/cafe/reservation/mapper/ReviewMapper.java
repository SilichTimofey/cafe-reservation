package com.cafe.reservation.mapper;

import com.cafe.reservation.dto.ReviewResponseDTO;
import com.cafe.reservation.model.Review;
import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewResponseDTO toResponse(Review r) {
        return new ReviewResponseDTO(
                r.getId(),
                r.getUser().getName(),
                r.getRating(),
                r.getComment(),
                r.getCreatedAt());
    }
}
