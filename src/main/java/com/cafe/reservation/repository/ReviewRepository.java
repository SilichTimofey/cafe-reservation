package com.cafe.reservation.repository;

import com.cafe.reservation.model.Review;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReviewRepository extends JpaRepository<Review, Long> {
}
