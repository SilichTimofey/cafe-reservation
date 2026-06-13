package com.cafe.reservation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ReservationApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ReservationApiApplication.class, args);
    }
}
