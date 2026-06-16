package com.cafe.reservation.repository;

import com.cafe.reservation.model.CafeTable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CafeTableRepository extends JpaRepository<CafeTable, Long> {

    boolean existsByTableNumber(String tableNumber);
}
