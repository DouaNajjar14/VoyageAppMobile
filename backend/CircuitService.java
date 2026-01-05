package com.ahmedyassin.TravelSmart.services;

import com.ahmedyassin.TravelSmart.entities.Circuit;
import com.ahmedyassin.TravelSmart.entities.CircuitDay;
import com.ahmedyassin.TravelSmart.entities.CircuitActivity;
import com.ahmedyassin.TravelSmart.repositories.CircuitRepository;
import com.ahmedyassin.TravelSmart.repositories.CircuitDayRepository;
import com.ahmedyassin.TravelSmart.repositories.CircuitActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class CircuitService {

    private final CircuitRepository circuitRepository;
    private final CircuitDayRepository circuitDayRepository;
    private final CircuitActivityRepository circuitActivityRepository;

    public List<Circuit> getAllActiveCircuits() {
        return circuitRepository.findByIsActiveTrue();
    }

    public Circuit getCircuitById(UUID id) {
        return circuitRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Circuit non trouvé: " + id));
    }

    public List<Circuit> searchCircuits(String destination, Integer duration, Double minPrice, Double maxPrice, String sortBy) {
        log.info("=== RECHERCHE CIRCUITS ===");
        log.info("Destination: {}", destination);
        log.info("Durée: {}", duration);
        log.info("Prix min: {}, Prix max: {}", minPrice, maxPrice);
        
        List<Circuit> circuits = circuitRepository.findByIsActiveTrue();
        log.info("Circuits actifs trouvés: {}", circuits.size());

        // Filtrer par destination
        if (destination != null && !destination.isEmpty()) {
            String destLower = destination.toLowerCase();
            log.info("Filtrage par destination: {}", destLower);
            
            circuits = circuits.stream()
                    .filter(c -> {
                        log.info("Circuit: {} - Destinations: {}", c.getTitle(), c.getDestinations());
                        if (c.getDestinations() != null && !c.getDestinations().isEmpty()) {
                            boolean match = c.getDestinations().stream()
                                    .anyMatch(d -> d.toLowerCase().contains(destLower));
                            log.info("  Match: {}", match);
                            return match;
                        }
                        log.info("  Pas de destinations");
                        return false;
                    })
                    .toList();
            
            log.info("Circuits après filtre destination: {}", circuits.size());
        }

        // Filtrer par durée
        if (duration != null) {
            log.info("Filtrage par durée: {}", duration);
            circuits = circuits.stream()
                    .filter(c -> c.getDuree() != null && c.getDuree().equals(duration))
                    .toList();
            log.info("Circuits après filtre durée: {}", circuits.size());
        }

        // Filtrer par prix minimum
        if (minPrice != null) {
            log.info("Filtrage par prix min: {}", minPrice);
            circuits = circuits.stream()
                    .filter(c -> c.getPrix() >= minPrice)
                    .toList();
            log.info("Circuits après filtre prix min: {}", circuits.size());
        }

        // Filtrer par prix maximum
        if (maxPrice != null) {
            log.info("Filtrage par prix max: {}", maxPrice);
            circuits = circuits.stream()
                    .filter(c -> c.getPrix() <= maxPrice)
                    .toList();
            log.info("Circuits après filtre prix max: {}", circuits.size());
        }

        // Trier par prix
        if ("price".equals(sortBy)) {
            circuits = circuits.stream()
                    .sorted((c1, c2) -> Double.compare(c1.getPrix(), c2.getPrix()))
                    .toList();
        }

        log.info("=== RÉSULTAT: {} circuits ===", circuits.size());
        return circuits;
    }
    
    public List<CircuitDay> getCircuitProgram(UUID circuitId) {
        log.info("Récupération du programme pour le circuit: {}", circuitId);
        return circuitDayRepository.findByCircuitIdOrderByDayNumberAsc(circuitId);
    }
    
    public List<CircuitActivity> getCircuitActivities(UUID circuitId) {
        log.info("Récupération des activités pour le circuit: {}", circuitId);
        return circuitActivityRepository.findByCircuitId(circuitId);
    }
    
    public Map<String, Object> calculatePrice(UUID circuitId, Map<String, Object> request) {
        Circuit circuit = getCircuitById(circuitId);
        
        int adults = (int) request.getOrDefault("adults", 1);
        List<Integer> children = (List<Integer>) request.getOrDefault("children", List.of());
        String hotelLevel = (String) request.getOrDefault("hotelLevel", "STANDARD");
        String flightClass = (String) request.getOrDefault("flightClass", "ECONOMY");
        List<String> selectedActivityIds = (List<String>) request.getOrDefault("selectedActivities", List.of());
        
        double basePrice = circuit.getPrix();
        
        // 1. Prix adultes
        double adultsPrice = adults * basePrice;
        
        // 2. Prix enfants (0-4 gratuit, 5-18 = 70%)
        int freeChildren = (int) children.stream().filter(age -> age <= 4).count();
        double childrenPrice = children.stream()
            .filter(age -> age > 4)
            .mapToDouble(age -> age <= 18 ? basePrice * 0.7 : basePrice)
            .sum();
        
        // 3. Supplément hôtel
        int payingPersons = adults + (int) children.stream().filter(age -> age > 4).count();
        double hotelExtra = getHotelExtra(hotelLevel) * payingPersons;
        
        // 4. Supplément vol
        double flightExtra = getFlightExtra(flightClass) * payingPersons;
        
        // 5. Activités optionnelles
        double activitiesPrice = selectedActivityIds.stream()
            .map(id -> {
                try {
                    return circuitActivityRepository.findById(UUID.fromString(id))
                        .map(CircuitActivity::getPrice)
                        .orElse(0.0);
                } catch (Exception e) {
                    return 0.0;
                }
            })
            .mapToDouble(Double::doubleValue)
            .sum();
        
        // Total
        double totalPrice = adultsPrice + childrenPrice + hotelExtra + flightExtra + activitiesPrice;
        
        Map<String, Object> breakdown = new HashMap<>();
        breakdown.put("basePrice", basePrice);
        breakdown.put("adults", adults);
        breakdown.put("adultsPrice", adultsPrice);
        breakdown.put("children", children);
        breakdown.put("childrenPrice", childrenPrice);
        breakdown.put("freeChildren", freeChildren);
        breakdown.put("hotelLevel", hotelLevel);
        breakdown.put("hotelExtra", hotelExtra);
        breakdown.put("flightClass", flightClass);
        breakdown.put("flightExtra", flightExtra);
        breakdown.put("activitiesPrice", activitiesPrice);
        breakdown.put("totalPrice", totalPrice);
        
        return breakdown;
    }
    
    private double getHotelExtra(String level) {
        switch (level) {
            case "SUPERIOR":
                return 200.0;
            case "LUXURY":
                return 450.0;
            default:
                return 0.0;
        }
    }
    
    private double getFlightExtra(String flightClass) {
        switch (flightClass) {
            case "BUSINESS":
                return 400.0;
            case "FIRST":
                return 900.0;
            default:
                return 0.0;
        }
    }
}
