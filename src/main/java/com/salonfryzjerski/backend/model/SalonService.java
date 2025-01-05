package com.salonfryzjerski.backend.model;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class SalonService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false)
    private int duration; // w minutach

    @Column(nullable = false)
    private double price;
}
