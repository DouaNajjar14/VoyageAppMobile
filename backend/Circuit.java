package com.ahmedyassin.TravelSmart.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "circuits")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Circuit {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String description;

    @Column(nullable = false)
    private Double prix;

    @Column(nullable = false)
    private Integer duree;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "circuit_destinations", joinColumns = @JoinColumn(name = "circuit_id"))
    @Column(name = "destination")
    private List<String> destinations = new ArrayList<>();

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "type")
    private String type; // CULTUREL, AVENTURE, DETENTE, MIXTE

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "circuit_included", joinColumns = @JoinColumn(name = "circuit_id"))
    @Column(name = "item")
    private List<String> included = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "circuit_highlights", joinColumns = @JoinColumn(name = "circuit_id"))
    @Column(name = "highlight")
    private List<String> highlights = new ArrayList<>();

    @OneToMany(mappedBy = "circuit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CircuitDay> program = new ArrayList<>();

    @OneToMany(mappedBy = "circuit", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CircuitActivity> activities = new ArrayList<>();

    @ManyToMany
    @JoinTable(
        name = "circuit_pays",
        joinColumns = @JoinColumn(name = "circuit_id"),
        inverseJoinColumns = @JoinColumn(name = "pays_id")
    )
    private List<Pays> pays = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
