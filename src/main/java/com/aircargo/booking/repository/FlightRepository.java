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

    @Query("SELECT f.flightNumber FROM Flight f WHERE f.origin = :origin AND f.departureTime >= :startOfDay AND f.departureTime < :endOfDay AND f.destination = :destination")
    List<String> findDirectFlightNumber(@Param("origin") String origin, @Param("startOfDay") Instant startOfDay, @Param("endOfDay") Instant endOfDay, @Param("destination") String destination);

    @Query("SELECT f1, f2 " +
            "FROM Flight f1 JOIN Flight f2 ON f1.destination = f2.origin " +
            "WHERE f1.origin = :origin " +
            "AND f2.destination = :destination " +
            "AND f1.departureTime >= :startDate " +
            "AND f1.departureTime < :endDate " +
            "AND f2.departureTime >= f1.arrivalTime " +
            "AND f2.departureTime < :secondLegEndDate")
    List<Object[]> findOneStopFlights(
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("secondLegEndDate") Instant secondLegEndDate);

    @Query("SELECT f1.flightNumber, f2.flightNumber " +
            "FROM Flight f1 JOIN Flight f2 ON f1.destination = f2.origin " +
            "WHERE f1.origin = :origin " +
            "AND f2.destination = :destination " +
            "AND f1.departureTime >= :startDate " +
            "AND f1.departureTime < :endDate " +
            "AND f2.departureTime >= f1.arrivalTime " +
            "AND f2.departureTime < :secondLegEndDate")
    List<Object[]> findOneStopFlightsNative(
            @Param("origin") String origin,
            @Param("destination") String destination,
            @Param("startDate") Instant startDate,
            @Param("endDate") Instant endDate,
            @Param("secondLegEndDate") Instant secondLegEndDate);

    Flight findByFlightNumber(String flightNumber);
}
