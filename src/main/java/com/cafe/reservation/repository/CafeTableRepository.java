package com.cafe.reservation.repository;

import com.cafe.reservation.model.CafeTable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CafeTableRepository extends JpaRepository<CafeTable, Long> {

    Optional<CafeTable> findByTableNumber(String tableNumber);

    boolean existsByTableNumber(String tableNumber);

    List<CafeTable> findByIsVip(boolean isVip);
}
