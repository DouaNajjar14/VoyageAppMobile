package com.ahmedyassin.TravelSmart.repositories;

import com.ahmedyassin.TravelSmart.entities.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface HotelRepository extends JpaRepository<Hotel, UUID> {
    List<Hotel> findByIsActiveTrue();
    List<Hotel> findByCityAndIsActiveTrue(String city);
    List<Hotel> findByEtoile(Integer etoile);
    List<Hotel> findTop10ByIsActiveTrueOrderByEtoileDesc();
    List<Hotel> findTop10ByIsActiveTrueOrderByPricePerNightAsc();
}
