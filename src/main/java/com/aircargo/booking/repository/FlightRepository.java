package com.aircargo.booking.repository;

import com.aircargo.booking.entity.Flight;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface FlightRepository extends JpaRepository<Flight, UUID> {

    @Query("SELECT f FROM Flight f WHERE f.origin = :origin AND f.departureTime >= :startOfDay AND f.departureTime < :endOfDay AND f.destination = :destination")
    List<Flight> findDirectFlight(@Param("origin") String origin, @Param("startOfDay") Instant startOfDay, @Param("endOfDay") Instant endOfDay, @Param("destination") String destination);

    @Query("SELECT f FROM Flight f WHERE f.origin = :origin AND f.departureTime >= :startOfDay AND f.departureTime < :endOfDay")
    List<Flight> findFirstLeg(@Param("origin") String origin, @Param("startOfDay") Instant startOfDay, @Param("endOfDay") Instant endOfDay);

    @Query("SELECT f FROM Flight f WHERE f.origin = :firstDestination AND f.departureTime >= :arrivalStart AND f.departureTime < :arrivalEnd AND f.destination = :destination")
    List<Flight> findSecondLeg(@Param("firstDestination") String firstDestination, @Param("arrivalStart") Instant arrivalStart, @Param("destination") String destination, @Param("arrivalEnd") Instant arrivalEnd);

    Flight findByFlightNumber(String flightNumber);
}
