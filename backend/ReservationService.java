package com.ahmedyassin.TravelSmart.services;

import com.ahmedyassin.TravelSmart.entities.*;
import com.ahmedyassin.TravelSmart.enums.ReservationStatus;
import com.ahmedyassin.TravelSmart.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final HotelRepository hotelRepository;
    private final FlightRepository flightRepository;
    private final CircuitRepository circuitRepository;
    private final RoomRepository roomRepository;

    public List<Reservation> getReservationsByEmail(String email) {
        log.info("Recherche des réservations pour: {}", email);
        return reservationRepository.findByClientEmailOrderByBookingDateDesc(email);
    }

    public Map<String, Object> getReservationDetails(UUID reservationId) {
        log.info("Récupération des détails de la réservation: {}", reservationId);
        
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        
        Map<String, Object> details = new HashMap<>();
        details.put("id", reservation.getId().toString());
        details.put("clientEmail", reservation.getClientEmail());
        details.put("offerType", reservation.getOfferType());
        details.put("offerName", reservation.getOfferName());
        details.put("price", reservation.getPrice());
        details.put("status", reservation.getStatus().toString());
        details.put("bookingDate", reservation.getBookingDate().toString());
        details.put("paymentMethod", reservation.getPaymentMethod());
        
        if (reservation.getStartDate() != null) {
            details.put("startDate", reservation.getStartDate().toString());
        }
        if (reservation.getEndDate() != null) {
            details.put("endDate", reservation.getEndDate().toString());
        }
        if (reservation.getAdultsCount() != null) {
            details.put("adultsCount", reservation.getAdultsCount());
        }
        if (reservation.getChildrenCount() != null) {
            details.put("childrenCount", reservation.getChildrenCount());
        }
        if (reservation.getFormula() != null) {
            details.put("formula", reservation.getFormula());
        }
        if (reservation.getChildrenAges() != null) {
            details.put("childrenAges", reservation.getChildrenAges());
        }
        if (reservation.getHotelLevel() != null) {
            details.put("hotelLevel", reservation.getHotelLevel());
        }
        if (reservation.getFlightClass() != null) {
            details.put("flightClass", reservation.getFlightClass());
        }
        if (reservation.getSelectedActivities() != null) {
            details.put("selectedActivities", reservation.getSelectedActivities());
        }
        if (reservation.getPriceBreakdown() != null) {
            details.put("priceBreakdown", reservation.getPriceBreakdown());
        }
        
        return details;
    }

    public Map<String, Object> createReservation(
            String email,
            UUID offerId,
            String offerType,
            Double price,
            String paymentMethod,
            String roomType,
            String checkInDate,
            String checkOutDate,
            Integer adultsCount,
            Integer childrenCount,
            String formula,
            String childrenAges,
            String hotelLevel,
            String flightClass,
            String selectedActivities,
            String priceBreakdown) {

        log.info("Création d'une réservation {} pour {}", offerType, email);

        Reservation reservation = new Reservation();
        reservation.setClientEmail(email);
        reservation.setOfferId(offerId);
        reservation.setOfferType(offerType);
        reservation.setPrice(price);
        reservation.setPaymentMethod(paymentMethod);
        reservation.setBookingDate(LocalDateTime.now());
        reservation.setStatus(ReservationStatus.CONFIRMED);
        reservation.setAdultsCount(adultsCount);
        reservation.setChildrenCount(childrenCount);
        reservation.setFormula(formula);
        
        // Stocker les détails spécifiques aux circuits
        if (childrenAges != null && !childrenAges.isEmpty()) {
            reservation.setChildrenAges(childrenAges);
            log.info("✅ childrenAges stocké: {}", childrenAges);
        } else {
            log.warn("⚠️ childrenAges est NULL ou vide");
        }
        if (hotelLevel != null && !hotelLevel.isEmpty()) {
            reservation.setHotelLevel(hotelLevel);
            log.info("✅ hotelLevel stocké: {}", hotelLevel);
        } else {
            log.warn("⚠️ hotelLevel est NULL ou vide");
        }
        if (flightClass != null && !flightClass.isEmpty()) {
            reservation.setFlightClass(flightClass);
            log.info("✅ flightClass stocké: {}", flightClass);
        } else {
            log.warn("⚠️ flightClass est NULL ou vide");
        }
        if (selectedActivities != null && !selectedActivities.isEmpty()) {
            reservation.setSelectedActivities(selectedActivities);
            log.info("✅ selectedActivities stocké: {}", selectedActivities);
        } else {
            log.warn("⚠️ selectedActivities est NULL ou vide");
        }
        if (priceBreakdown != null && !priceBreakdown.isEmpty()) {
            reservation.setPriceBreakdown(priceBreakdown);
            log.info("✅ priceBreakdown stocké: {}", priceBreakdown);
        } else {
            log.warn("⚠️ priceBreakdown est NULL ou vide");
        }

        // Parser les dates si disponibles
        if (checkInDate != null && !checkInDate.isEmpty()) {
            try {
                reservation.setStartDate(LocalDate.parse(checkInDate));
            } catch (Exception e) {
                log.warn("Impossible de parser checkInDate: {}", checkInDate);
            }
        }
        
        if (checkOutDate != null && !checkOutDate.isEmpty()) {
            try {
                reservation.setEndDate(LocalDate.parse(checkOutDate));
            } catch (Exception e) {
                log.warn("Impossible de parser checkOutDate: {}", checkOutDate);
            }
        }

        // Récupérer le nom de l'offre selon le type
        String offerName = "";
        try {
            switch (offerType.toLowerCase()) {
                case "hotel":
                    Hotel hotel = hotelRepository.findById(offerId)
                            .orElseThrow(() -> new RuntimeException("Hôtel non trouvé"));
                    offerName = hotel.getName();
                    
                    // Trouver le room_id si roomType est fourni
                    if (roomType != null && !roomType.isEmpty()) {
                        Optional<Room> room = roomRepository.findByHotelIdAndRoomType(offerId, roomType);
                        room.ifPresent(r -> reservation.setRoomId(r.getId()));
                    }
                    break;
                    
                case "flight":
                    Flight flight = flightRepository.findById(offerId)
                            .orElseThrow(() -> new RuntimeException("Vol non trouvé"));
                    offerName = flight.getAirline() + " - " + flight.getOrigin() + " → " + flight.getDestination();
                    break;
                    
                case "circuit":
                    Circuit circuit = circuitRepository.findById(offerId)
                            .orElseThrow(() -> new RuntimeException("Circuit non trouvé"));
                    offerName = circuit.getTitle();
                    break;
                    
                default:
                    offerName = "Offre #" + offerId;
            }
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du nom de l'offre: {}", e.getMessage());
            offerName = "Offre #" + offerId;
        }
        
        reservation.setOfferName(offerName);

        // Sauvegarder la réservation
        Reservation savedReservation = reservationRepository.save(reservation);
        log.info("✅ Réservation créée: {}", savedReservation.getId());

        // Préparer la réponse
        Map<String, Object> response = new HashMap<>();
        response.put("reservationId", savedReservation.getId().toString());
        response.put("status", savedReservation.getStatus().toString());
        response.put("message", "Réservation créée avec succès");
        response.put("offerName", offerName);
        response.put("price", price);

        return response;
    }
}
