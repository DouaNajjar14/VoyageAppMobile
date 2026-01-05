package com.ahmedyassin.TravelSmart.services;

import com.ahmedyassin.TravelSmart.entities.Hotel;
import com.ahmedyassin.TravelSmart.repositories.HotelRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class HotelService {

    private final HotelRepository hotelRepository;

    public List<Hotel> getAllActiveHotels() {
        return hotelRepository.findByIsActiveTrue();
    }

    public Hotel getHotelById(UUID id) {
        return hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hôtel non trouvé: " + id));
    }

    public List<Hotel> getHotelsByCity(String city) {
        return hotelRepository.findByCityAndIsActiveTrue(city);
    }

    public List<Hotel> getHotelsByStars(Integer etoile) {
        return hotelRepository.findByEtoile(etoile);
    }

    public List<Hotel> searchHotels(String destination, Double minPrice, Double maxPrice, String sortBy, String sortOrder) {
        List<Hotel> hotels = hotelRepository.findByIsActiveTrue();

        // Filtrer par destination (ville)
        if (destination != null && !destination.isEmpty()) {
            hotels = hotels.stream()
                    .filter(h -> h.getCity() != null && h.getCity().toLowerCase().contains(destination.toLowerCase()))
                    .toList();
        }

        // Filtrer par prix minimum
        if (minPrice != null) {
            hotels = hotels.stream()
                    .filter(h -> h.getPricePerNight() >= minPrice)
                    .toList();
        }

        // Filtrer par prix maximum
        if (maxPrice != null) {
            hotels = hotels.stream()
                    .filter(h -> h.getPricePerNight() <= maxPrice)
                    .toList();
        }

        // Trier
        if ("price".equals(sortBy)) {
            if ("desc".equals(sortOrder)) {
                hotels = hotels.stream()
                        .sorted((h1, h2) -> Double.compare(h2.getPricePerNight(), h1.getPricePerNight()))
                        .toList();
            } else {
                hotels = hotels.stream()
                        .sorted((h1, h2) -> Double.compare(h1.getPricePerNight(), h2.getPricePerNight()))
                        .toList();
            }
        }

        return hotels;
    }
}
