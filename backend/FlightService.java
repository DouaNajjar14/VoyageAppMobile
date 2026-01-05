package com.ahmedyassin.TravelSmart.services;

import com.ahmedyassin.TravelSmart.entities.Flight;
import com.ahmedyassin.TravelSmart.repositories.FlightRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;

    public List<Flight> getAllActiveFlights() {
        return flightRepository.findByIsActiveTrue();
    }

    public Flight getFlightById(UUID id) {
        return flightRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Vol non trouvé: " + id));
    }

    public List<Flight> searchFlights(String origin, String destination, Double minPrice, Double maxPrice, String sortBy) {
        List<Flight> flights = flightRepository.findByIsActiveTrue();

        // Filtrer par origine
        if (origin != null && !origin.isEmpty()) {
            flights = flights.stream()
                    .filter(f -> f.getOrigin() != null && f.getOrigin().toLowerCase().contains(origin.toLowerCase()))
                    .toList();
        }

        // Filtrer par destination
        if (destination != null && !destination.isEmpty()) {
            flights = flights.stream()
                    .filter(f -> f.getDestination() != null && f.getDestination().toLowerCase().contains(destination.toLowerCase()))
                    .toList();
        }

        // Filtrer par prix minimum
        if (minPrice != null) {
            flights = flights.stream()
                    .filter(f -> f.getPrice() >= minPrice)
                    .toList();
        }

        // Filtrer par prix maximum
        if (maxPrice != null) {
            flights = flights.stream()
                    .filter(f -> f.getPrice() <= maxPrice)
                    .toList();
        }

        // Trier par prix
        if ("price".equals(sortBy)) {
            flights = flights.stream()
                    .sorted((f1, f2) -> Double.compare(f1.getPrice(), f2.getPrice()))
                    .toList();
        }

        return flights;
    }
    
    public List<Flight> searchFlightsByDateAndPassengers(
            String origin, 
            String destination, 
            String departureDate,
            int totalPassengers) {
        
        log.info("🔍 Recherche de vols: {} → {}, date: {}, passagers: {}", 
            origin, destination, departureDate, totalPassengers);
        
        List<Flight> flights = flightRepository.findByIsActiveTrue();
        
        // Filtrer par origine et destination
        flights = flights.stream()
                .filter(f -> f.getOrigin() != null && 
                           f.getOrigin().toUpperCase().contains(origin.toUpperCase()))
                .filter(f -> f.getDestination() != null && 
                           f.getDestination().toUpperCase().contains(destination.toUpperCase()))
                .filter(f -> f.getSeatsAvailable() >= totalPassengers)
                .toList();
        
        log.info("✅ {} vol(s) trouvé(s)", flights.size());
        return flights;
    }
}
