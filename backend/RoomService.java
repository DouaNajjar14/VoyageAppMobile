package com.ahmedyassin.TravelSmart.services;

import com.ahmedyassin.TravelSmart.entities.Room;
import com.ahmedyassin.TravelSmart.repositories.RoomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;

    public List<Room> getRoomsByHotelId(UUID hotelId) {
        return roomRepository.findByHotelIdAndIsAvailableTrue(hotelId);
    }

    public List<Room> getAllRoomsByHotelId(UUID hotelId) {
        return roomRepository.findByHotelId(hotelId);
    }

    public Room getRoomById(UUID id) {
        return roomRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Chambre non trouvée: " + id));
    }

    public Room createRoom(Room room) {
        return roomRepository.save(room);
    }

    public Room updateRoom(UUID id, Room roomDetails) {
        Room room = getRoomById(id);
        
        if (roomDetails.getRoomNumber() != null) {
            room.setRoomNumber(roomDetails.getRoomNumber());
        }
        if (roomDetails.getRoomType() != null) {
            room.setRoomType(roomDetails.getRoomType());
        }
        if (roomDetails.getPrice() != null) {
            room.setPrice(roomDetails.getPrice());
        }
        if (roomDetails.getMaxOccupancy() != null) {
            room.setMaxOccupancy(roomDetails.getMaxOccupancy());
        }
        if (roomDetails.getDescription() != null) {
            room.setDescription(roomDetails.getDescription());
        }
        if (roomDetails.getViewType() != null) {
            room.setViewType(roomDetails.getViewType());
        }
        if (roomDetails.getBedType() != null) {
            room.setBedType(roomDetails.getBedType());
        }
        if (roomDetails.getSizeSqm() != null) {
            room.setSizeSqm(roomDetails.getSizeSqm());
        }
        if (roomDetails.getIsAvailable() != null) {
            room.setIsAvailable(roomDetails.getIsAvailable());
        }
        
        return roomRepository.save(room);
    }

    public void deleteRoom(UUID id) {
        Room room = getRoomById(id);
        room.setIsAvailable(false);
        roomRepository.save(room);
    }
}
