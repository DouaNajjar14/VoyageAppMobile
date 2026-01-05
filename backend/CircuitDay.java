package com.ahmedyassin.TravelSmart.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "circuit_days")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CircuitDay {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "circuit_id", nullable = false)
    @JsonBackReference
    private Circuit circuit;

    @Column(name = "day_number", nullable = false)
    private Integer dayNumber;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @ElementCollection
    @CollectionTable(name = "circuit_day_meals", joinColumns = @JoinColumn(name = "circuit_day_id"))
    @Column(name = "meal")
    private List<String> meals = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "circuit_day_activities", joinColumns = @JoinColumn(name = "circuit_day_id"))
    @Column(name = "activity")
    private List<String> activities = new ArrayList<>();
}
