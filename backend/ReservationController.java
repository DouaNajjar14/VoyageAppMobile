package com.ahmedyassin.TravelSmart.controllers;

import com.ahmedyassin.TravelSmart.entities.Reservation;
import com.ahmedyassin.TravelSmart.services.ReservationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/reservation")
@RequiredArgsConstructor
public class ReservationController {

    private final ReservationService reservationService;

    @GetMapping
    public ResponseEntity<?> getReservations(@RequestParam String email) {
        log.info("GET /api/reservation?email={}", email);
        
        try {
            List<Reservation> reservations = reservationService.getReservationsByEmail(email);
            log.info("✅ {} réservation(s) trouvée(s) pour {}", reservations.size(), email);
            
            // Convertir en Map pour s'assurer que tous les champs sont inclus
            List<Map<String, Object>> response = new java.util.ArrayList<>();
            for (Reservation r : reservations) {
                Map<String, Object> resMap = new HashMap<>();
                resMap.put("id", r.getId().toString());
                resMap.put("clientEmail", r.getClientEmail());
                resMap.put("offerType", r.getOfferType());
                resMap.put("offerId", r.getOfferId().toString());
                resMap.put("offerName", r.getOfferName());
                resMap.put("price", r.getPrice());
                resMap.put("bookingDate", r.getBookingDate().toString());
                resMap.put("status", r.getStatus().toString());
                resMap.put("paymentMethod", r.getPaymentMethod());
                
                if (r.getStartDate() != null) resMap.put("startDate", r.getStartDate().toString());
                if (r.getEndDate() != null) resMap.put("endDate", r.getEndDate().toString());
                if (r.getDepartureTime() != null) resMap.put("departureTime", r.getDepartureTime().toString());
                if (r.getArrivalTime() != null) resMap.put("arrivalTime", r.getArrivalTime().toString());
                if (r.getAdultsCount() != null) resMap.put("adultsCount", r.getAdultsCount());
                if (r.getChildrenCount() != null) resMap.put("childrenCount", r.getChildrenCount());
                if (r.getFormula() != null) resMap.put("formula", r.getFormula());
                if (r.getChildrenAges() != null) resMap.put("childrenAges", r.getChildrenAges());
                if (r.getHotelLevel() != null) resMap.put("hotelLevel", r.getHotelLevel());
                if (r.getFlightClass() != null) resMap.put("flightClass", r.getFlightClass());
                if (r.getSelectedActivities() != null) resMap.put("selectedActivities", r.getSelectedActivities());
                if (r.getPriceBreakdown() != null) resMap.put("priceBreakdown", r.getPriceBreakdown());
                
                response.add(resMap);
                
                // Log pour debug
                if ("circuit".equals(r.getOfferType())) {
                    log.info("Circuit {} - hotelLevel: {}, flightClass: {}", 
                        r.getOfferName(), r.getHotelLevel(), r.getFlightClass());
                }
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des réservations: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @GetMapping("/{reservationId}/details")
    public ResponseEntity<?> getReservationDetails(@PathVariable String reservationId) {
        log.info("GET /api/reservation/{}/details", reservationId);
        
        try {
            UUID id = UUID.fromString(reservationId);
            Map<String, Object> details = reservationService.getReservationDetails(id);
            log.info("✅ Détails de la réservation {} récupérés", reservationId);
            return ResponseEntity.ok(details);
        } catch (Exception e) {
            log.error("❌ Erreur lors de la récupération des détails: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<?> createReservation(@RequestBody Map<String, String> body) {

        log.info("POST /api/reservation");
        log.info("Body reçu: {}", body);

        try {
            // Extraire les paramètres du body
            String email = body.get("email");
            String paymentMethod = body.get("paymentMethod");
            
            // Déterminer le type d'offre et l'ID
            String offerType = null;
            UUID offerId = null;
            Double price = null;
            
            if (body.containsKey("hotelId")) {
                offerType = "hotel";
                offerId = UUID.fromString(body.get("hotelId"));
                price = Double.parseDouble(body.get("totalPrice"));
                log.info("Réservation HOTEL: hotelId={}, roomType={}, dates={} - {}", 
                    body.get("hotelId"), body.get("roomType"), 
                    body.get("checkInDate"), body.get("checkOutDate"));
            } else if (body.containsKey("flightId")) {
                offerType = "flight";
                offerId = UUID.fromString(body.get("flightId"));
                // Le prix peut être dans "totalPrice" ou "price"
                price = body.containsKey("totalPrice") ? Double.parseDouble(body.get("totalPrice")) :
                        body.containsKey("price") ? Double.parseDouble(body.get("price")) : 0.0;
                log.info("Réservation FLIGHT: flightId={}, price={}", body.get("flightId"), price);
            } else if (body.containsKey("circuitId")) {
                offerType = "circuit";
                offerId = UUID.fromString(body.get("circuitId"));
                // Le prix peut être dans "totalPrice" ou "price"
                price = body.containsKey("totalPrice") ? Double.parseDouble(body.get("totalPrice")) :
                        body.containsKey("price") ? Double.parseDouble(body.get("price")) : 0.0;
                log.info("Réservation CIRCUIT: circuitId={}, price={}", body.get("circuitId"), price);
            } else {
                log.error("❌ Type d'offre non reconnu dans le body");
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Type d'offre manquant (hotelId, flightId ou circuitId requis)"));
            }

            // Extraire le roomType si disponible
            String roomType = body.get("roomType");
            
            // Extraire les dates et le nombre de personnes
            String checkInDate = body.get("checkInDate");
            String checkOutDate = body.get("checkOutDate");
            Integer adultsCount = body.containsKey("adultsCount") ? Integer.parseInt(body.get("adultsCount")) : null;
            Integer childrenCount = body.containsKey("childrenCount") ? Integer.parseInt(body.get("childrenCount")) : null;
            String formula = body.get("formula"); // petit_dejeuner, demi_pension, pension_complete, all_inclusive
            
            // Extraire les détails spécifiques aux circuits
            String childrenAges = body.get("childrenAges"); // JSON array
            String hotelLevel = body.get("hotelLevel"); // STANDARD, SUPERIOR, LUXURY
            String flightClass = body.get("flightClass"); // ECONOMY, BUSINESS, FIRST
            String selectedActivities = body.get("selectedActivities"); // JSON
            String priceBreakdown = body.get("priceBreakdown"); // JSON
            
            log.info("Params extraits: email={}, offerId={}, offerType={}, price={}, paymentMethod={}, roomType={}, formula={}",
                    email, offerId, offerType, price, paymentMethod, roomType, formula);
            
            // LOG DÉTAILLÉ POUR LES CIRCUITS
            if ("circuit".equals(offerType)) {
                log.info("=== DÉTAILS CIRCUIT ===");
                log.info("  adultsCount: {}", adultsCount);
                log.info("  childrenCount: {}", childrenCount);
                log.info("  childrenAges: {}", childrenAges);
                log.info("  hotelLevel: {}", hotelLevel);
                log.info("  flightClass: {}", flightClass);
                log.info("  selectedActivities: {}", selectedActivities);
                log.info("  priceBreakdown: {}", priceBreakdown);
                log.info("=======================");
            }

            Map<String, Object> response = reservationService.createReservation(
                    email, offerId, offerType, price, paymentMethod, roomType, 
                    checkInDate, checkOutDate, adultsCount, childrenCount, formula,
                    childrenAges, hotelLevel, flightClass, selectedActivities, priceBreakdown);
            
            log.info("✅ Réservation créée avec succès");
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            log.error("❌ Erreur de validation: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Données invalides: " + e.getMessage()));
        } catch (Exception e) {
            log.error("❌ Erreur: {}", e.getMessage(), e);
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", e.getMessage()));
        }
    }
    
    @DeleteMapping("/{reservationId}")
    public ResponseEntity<?> cancelReservation(@PathVariable String reservationId) {
        log.info("DELETE /api/reservation/{}", reservationId);
        
        try {
            UUID id = UUID.fromString(reservationId);
            Map<String, Object> result = reservationService.cancelReservation(id);
            
            if ((Boolean) result.get("success")) {
                log.info("✅ Réservation {} annulée", reservationId);
                return ResponseEntity.ok(result);
            } else {
                log.warn("⚠️ Annulation refusée: {}", result.get("error"));
                return ResponseEntity.badRequest().body(result);
            }
        } catch (IllegalArgumentException e) {
            log.error("❌ ID invalide: {}", reservationId);
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "error", "ID de réservation invalide"));
        } catch (Exception e) {
            log.error("❌ Erreur lors de l'annulation: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("success", false, "error", e.getMessage()));
        }
    }
}
