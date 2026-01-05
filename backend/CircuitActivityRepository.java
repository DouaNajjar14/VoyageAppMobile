package com.ahmedyassin.TravelSmart.repositories;

import com.ahmedyassin.TravelSmart.entities.CircuitActivity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CircuitActivityRepository extends JpaRepository<CircuitActivity, UUID> {
    List<CircuitActivity> findByCircuitId(UUID circuitId);
    List<CircuitActivity> findByCircuitIdAndIsOptionalTrue(UUID circuitId);
}
