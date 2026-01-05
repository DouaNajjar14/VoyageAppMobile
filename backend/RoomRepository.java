package com.ahmedyassin.TravelSmart.repositories;

import com.ahmedyassin.TravelSmart.entities.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
    List<Room> findByHotelIdAndIsAvailableTrue(UUID hotelId);
    List<Room> findByHotelId(UUID hotelId);
    java.util.Optional<Room> findByHotelIdAndRoomType(UUID hotelId, String roomType);
}
