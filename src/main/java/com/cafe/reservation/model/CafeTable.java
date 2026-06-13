package com.cafe.reservation.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "cafe_tables")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CafeTable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tableNumber;

    private Integer capacity;

    private boolean isVip;
}
