package com.ahmedyassin.TravelSmart.repositories;

import com.ahmedyassin.TravelSmart.entities.CircuitDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CircuitDayRepository extends JpaRepository<CircuitDay, UUID> {
    List<CircuitDay> findByCircuitIdOrderByDayNumberAsc(UUID circuitId);
}
