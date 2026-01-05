package com.ahmedyassin.TravelSmart.entities;

import com.ahmedyassin.TravelSmart.enums.ReservationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "reservations")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(name = "client_email", nullable = false)
    private String clientEmail;

    @Column(name = "offer_id", nullable = false)
    private UUID offerId;

    @Column(name = "offer_type", nullable = false)
    private String offerType;

    @Column(name = "offer_name")
    private String offerName;

    @Column(name = "booking_date", nullable = false)
    private LocalDateTime bookingDate;

    @Column(name = "payment_method")
    private String paymentMethod;

    @Column(name = "room_id", nullable = true)
    private UUID roomId;

    @Column(nullable = false)
    private Double price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ReservationStatus status;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "adults_count")
    private Integer adultsCount;

    @Column(name = "children_count")
    private Integer childrenCount;
    
    @Column(name = "formula")
    private String formula; // "petit_dejeuner", "demi_pension", "pension_complete", "all_inclusive"
    
    // Champs spécifiques aux circuits
    @Column(name = "children_ages", columnDefinition = "TEXT")
    private String childrenAges; // JSON array des âges des enfants: "[3, 7, 12]"
    
    @Column(name = "hotel_level")
    private String hotelLevel; // "STANDARD", "SUPERIOR", "LUXURY"
    
    @Column(name = "flight_class")
    private String flightClass; // "ECONOMY", "BUSINESS", "FIRST"
    
    @Column(name = "selected_activities", columnDefinition = "TEXT")
    private String selectedActivities; // JSON des activités sélectionnées
    
    @Column(name = "price_breakdown", columnDefinition = "TEXT")
    private String priceBreakdown; // JSON du détail des prix

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (status == null) {
            status = ReservationStatus.PENDING;
        }
    }
}
