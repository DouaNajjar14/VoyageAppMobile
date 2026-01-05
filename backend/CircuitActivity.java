package com.ahmedyassin.TravelSmart.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "circuit_activities")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CircuitActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "circuit_id", nullable = false)
    @JsonBackReference
    private Circuit circuit;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private String duration;

    @Column(nullable = false)
    private Double price;

    @Column(name = "is_optional", nullable = false)
    private Boolean isOptional = true;

    @Column(name = "image_url")
    private String imageUrl;
}
