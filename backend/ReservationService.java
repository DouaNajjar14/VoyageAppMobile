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
        if (reservation.getDepartureTime() != null) {
            details.put("departureTime", reservation.getDepartureTime().toString());
        }
        if (reservation.getArrivalTime() != null) {
            details.put("arrivalTime", reservation.getArrivalTime().toString());
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
        
        // LOGS DÉTAILLÉS POUR HÔTEL
        if ("hotel".equals(offerType)) {
            log.info("=== DÉTAILS HÔTEL REÇUS ===");
            log.info("  checkInDate: {}", checkInDate);
            log.info("  checkOutDate: {}", checkOutDate);
            log.info("  adultsCount: {}", adultsCount);
            log.info("  childrenCount: {}", childrenCount);
            log.info("  formula: {}", formula);
            log.info("  priceBreakdown: {}", priceBreakdown);
            log.info("  roomType: {}", roomType);
            log.info("  price: {}", price);
            log.info("===========================");
        }

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
                // Essayer d'abord le format yyyy-MM-dd
                reservation.setStartDate(LocalDate.parse(checkInDate));
            } catch (Exception e1) {
                try {
                    // Essayer le format dd/MM/yyyy
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    reservation.setStartDate(LocalDate.parse(checkInDate, formatter));
                    log.info("✅ checkInDate parsé avec format dd/MM/yyyy: {}", checkInDate);
                } catch (Exception e2) {
                    log.warn("Impossible de parser checkInDate: {}", checkInDate);
                }
            }
        }
        
        if (checkOutDate != null && !checkOutDate.isEmpty()) {
            try {
                // Essayer d'abord le format yyyy-MM-dd
                reservation.setEndDate(LocalDate.parse(checkOutDate));
            } catch (Exception e1) {
                try {
                    // Essayer le format dd/MM/yyyy
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    reservation.setEndDate(LocalDate.parse(checkOutDate, formatter));
                    log.info("✅ checkOutDate parsé avec format dd/MM/yyyy: {}", checkOutDate);
                } catch (Exception e2) {
                    log.warn("Impossible de parser checkOutDate: {}", checkOutDate);
                }
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
                    
                    // Stocker les heures de départ et d'arrivée du vol
                    if (flight.getDepartureTime() != null) {
                        reservation.setDepartureTime(flight.getDepartureTime());
                        // Extraire la date de départ si pas déjà définie
                        if (reservation.getStartDate() == null) {
                            reservation.setStartDate(flight.getDepartureTime().toLocalDate());
                        }
                        log.info("✅ Heure de départ du vol stockée: {}", flight.getDepartureTime());
                    }
                    if (flight.getArrivalTime() != null) {
                        reservation.setArrivalTime(flight.getArrivalTime());
                        // Extraire la date d'arrivée si pas déjà définie
                        if (reservation.getEndDate() == null) {
                            reservation.setEndDate(flight.getArrivalTime().toLocalDate());
                        }
                        log.info("✅ Heure d'arrivée du vol stockée: {}", flight.getArrivalTime());
                    }
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

    public Map<String, Object> cancelReservation(UUID reservationId) {
        log.info("Tentative d'annulation de la réservation: {}", reservationId);
        
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new RuntimeException("Réservation non trouvée"));
        
        Map<String, Object> result = new HashMap<>();
        
        // 1️⃣ Vérifier le statut
        if (reservation.getStatus() != ReservationStatus.CONFIRMED) {
            result.put("success", false);
            result.put("error", "Seules les réservations confirmées peuvent être annulées");
            result.put("currentStatus", reservation.getStatus().toString());
            return result;
        }
        
        // 2️⃣ Vérifier la date/heure de départ
        LocalDate startDate = reservation.getStartDate();
        LocalDateTime departureTime = reservation.getDepartureTime();
        
        if (startDate == null) {
            result.put("success", false);
            result.put("error", "Date de départ non définie");
            return result;
        }
        
        LocalDate now = LocalDate.now();
        LocalDateTime nowDateTime = LocalDateTime.now();
        
        // Pour les vols, utiliser l'heure exacte si disponible
        if ("flight".equalsIgnoreCase(reservation.getOfferType()) && departureTime != null) {
            if (!nowDateTime.isBefore(departureTime)) {
                result.put("success", false);
                result.put("error", "Impossible d'annuler une réservation dont la date/heure est dépassée");
                return result;
            }
        } else {
            // Pour les autres types, vérifier juste la date
            if (!now.isBefore(startDate)) {
                result.put("success", false);
                result.put("error", "Impossible d'annuler une réservation dont la date est dépassée");
                return result;
            }
        }
        
        // 3️⃣ Calculer le délai restant
        long daysUntilStart = java.time.temporal.ChronoUnit.DAYS.between(now, startDate);
        long hoursUntilStart;
        
        // Pour les vols, utiliser l'heure exacte si disponible
        if ("flight".equalsIgnoreCase(reservation.getOfferType()) && departureTime != null) {
            hoursUntilStart = java.time.temporal.ChronoUnit.HOURS.between(nowDateTime, departureTime);
            log.info("Vol - Délai avant départ: {} jours ({} heures jusqu'à {})", 
                daysUntilStart, hoursUntilStart, departureTime);
        } else {
            // Pour les autres types, calculer jusqu'à 23h59 du jour de départ
            LocalDateTime endOfStartDate = startDate.atTime(23, 59, 59);
            hoursUntilStart = java.time.temporal.ChronoUnit.HOURS.between(nowDateTime, endOfStartDate);
            log.info("Délai avant départ: {} jours ({} heures jusqu'à fin de journée)", 
                daysUntilStart, hoursUntilStart);
        }
        
        // 4️⃣ Appliquer la politique d'annulation
        String offerType = reservation.getOfferType().toLowerCase();
        boolean canCancel = false;
        double refundPercentage = 0.0;
        String penaltyMessage = "";
        
        switch (offerType) {
            case "flight":
                if (hoursUntilStart >= 48) {
                    canCancel = true;
                    refundPercentage = 100.0;
                    penaltyMessage = "Annulation gratuite (48h ou plus avant le départ)";
                } else if (hoursUntilStart >= 24) {
                    canCancel = true;
                    refundPercentage = 100.0;
                    penaltyMessage = "Annulation autorisée (24-47h avant le départ)";
                } else {
                    canCancel = false;
                    penaltyMessage = "Annulation interdite (moins de 24h avant le départ)";
                }
                break;
                
            case "hotel":
                if (hoursUntilStart >= 24) {
                    canCancel = true;
                    refundPercentage = 100.0;
                    penaltyMessage = "Annulation gratuite (24h ou plus avant le check-in)";
                } else if (hoursUntilStart >= 12) {
                    canCancel = true;
                    refundPercentage = 50.0;
                    penaltyMessage = "Annulation avec pénalité de 50% (12-23h avant le check-in)";
                } else {
                    canCancel = false;
                    penaltyMessage = "Annulation interdite (moins de 12h avant le check-in)";
                }
                break;
                
            case "circuit":
                if (daysUntilStart >= 7) {
                    canCancel = true;
                    refundPercentage = 80.0;
                    penaltyMessage = "Annulation avec pénalité de 20% (7 jours ou plus avant le départ)";
                } else if (daysUntilStart >= 3) {
                    canCancel = true;
                    refundPercentage = 50.0;
                    penaltyMessage = "Annulation avec pénalité de 50% (3-6 jours avant le départ)";
                } else {
                    canCancel = false;
                    penaltyMessage = "Annulation interdite (moins de 3 jours avant le départ)";
                }
                break;
                
            default:
                result.put("success", false);
                result.put("error", "Type d'offre non reconnu");
                return result;
        }
        
        if (!canCancel) {
            result.put("success", false);
            result.put("error", penaltyMessage);
            result.put("canCancel", false);
            result.put("daysUntilStart", daysUntilStart);
            result.put("hoursUntilStart", hoursUntilStart);
            return result;
        }
        
        // 5️⃣ Calculer le remboursement
        double refundAmount = reservation.getPrice() * (refundPercentage / 100.0);
        double penaltyAmount = reservation.getPrice() - refundAmount;
        
        // 6️⃣ Mettre à jour la réservation
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservationRepository.save(reservation);
        
        log.info("✅ Réservation {} annulée avec succès", reservationId);
        
        // 7️⃣ Préparer la réponse
        result.put("success", true);
        result.put("message", "Réservation annulée avec succès");
        result.put("reservationId", reservationId.toString());
        result.put("offerType", offerType);
        result.put("offerName", reservation.getOfferName());
        result.put("originalPrice", reservation.getPrice());
        result.put("refundPercentage", refundPercentage);
        result.put("refundAmount", refundAmount);
        result.put("penaltyAmount", penaltyAmount);
        result.put("penaltyMessage", penaltyMessage);
        result.put("daysUntilStart", daysUntilStart);
        result.put("clientEmail", reservation.getClientEmail());
        
        return result;
    }
}
