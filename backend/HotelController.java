package com.ahmedyassin.TravelSmart.controllers;

import com.ahmedyassin.TravelSmart.entities.Hotel;
import com.ahmedyassin.TravelSmart.services.HotelService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/hotels")
@RequiredArgsConstructor
public class HotelController {

    private final HotelService hotelService;

    @GetMapping
    public ResponseEntity<List<Hotel>> getAllHotels() {
        log.info("GET /api/hotels");
        List<Hotel> hotels = hotelService.getAllActiveHotels();
        log.info("✅ {} hôtel(s) trouvé(s)", hotels.size());
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Hotel> getHotelById(@PathVariable UUID id) {
        log.info("GET /api/hotels/{}", id);
        Hotel hotel = hotelService.getHotelById(id);
        return ResponseEntity.ok(hotel);
    }

    @GetMapping("/city/{city}")
    public ResponseEntity<List<Hotel>> getHotelsByCity(@PathVariable String city) {
        log.info("GET /api/hotels/city/{}", city);
        List<Hotel> hotels = hotelService.getHotelsByCity(city);
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/stars/{stars}")
    public ResponseEntity<List<Hotel>> getHotelsByStars(@PathVariable Integer stars) {
        log.info("GET /api/hotels/stars/{}", stars);
        List<Hotel> hotels = hotelService.getHotelsByStars(stars);
        return ResponseEntity.ok(hotels);
    }

    @GetMapping("/search")
    public ResponseEntity<List<Hotel>> searchHotels(
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortOrder) {
        log.info("GET /api/hotels/search?destination={}&minPrice={}&maxPrice={}", destination, minPrice, maxPrice);
        List<Hotel> hotels = hotelService.searchHotels(destination, minPrice, maxPrice, sortBy, sortOrder);
        log.info("✅ {} hôtel(s) trouvé(s)", hotels.size());
        return ResponseEntity.ok(hotels);
    }
}
