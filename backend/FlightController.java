package com.ahmedyassin.TravelSmart.controllers;

import com.ahmedyassin.TravelSmart.entities.Flight;
import com.ahmedyassin.TravelSmart.services.FlightService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/flights")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping
    public ResponseEntity<List<Flight>> getAllFlights() {
        log.info("GET /api/flights");
        List<Flight> flights = flightService.getAllActiveFlights();
        log.info("✅ {} vol(s) trouvé(s)", flights.size());
        return ResponseEntity.ok(flights);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Flight> getFlightById(@PathVariable UUID id) {
        log.info("GET /api/flights/{}", id);
        Flight flight = flightService.getFlightById(id);
        return ResponseEntity.ok(flight);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Flight>> searchFlights(
            @RequestParam(required = false) String origin,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String departureDate,
            @RequestParam(required = false) Integer totalPassengers) {
        
        log.info("GET /api/flights/search?origin={}&destination={}&departureDate={}&totalPassengers={}", 
            origin, destination, departureDate, totalPassengers);
        
        List<Flight> flights;
        
        // Nouvelle recherche avec date et passagers
        if (departureDate != null && totalPassengers != null) {
            flights = flightService.searchFlightsByDateAndPassengers(
                origin, destination, departureDate, totalPassengers);
        } else {
            // Ancienne recherche (compatibilité)
            flights = flightService.searchFlights(origin, destination, minPrice, maxPrice, sortBy);
        }
        
        log.info("✅ {} vol(s) trouvé(s)", flights.size());
        return ResponseEntity.ok(flights);
    }
}
