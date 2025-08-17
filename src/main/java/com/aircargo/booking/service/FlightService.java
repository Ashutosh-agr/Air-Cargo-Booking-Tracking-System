package com.aircargo.booking.service;

import com.aircargo.booking.dto.FlightRequests;
import com.aircargo.booking.dto.FlightRouteResponse;
import com.aircargo.booking.dto.OneStopFlightResponse;
import com.aircargo.booking.entity.Flight;
import com.aircargo.booking.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;

    @Cacheable(cacheNames = "routeCache", key = "T(java.util.Objects).hash(#origin, #destination, #departureDate)")
    public FlightRouteResponse getRoutes(String origin, String destination, LocalDate departureDate){
        Instant startOfDay = departureDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfDay = departureDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        List<Flight> directFlight = flightRepository.findDirectFlight(origin, startOfDay, endOfDay, destination);

        List<OneStopFlightResponse> oneStopFlight = new ArrayList<>();

        List<Flight> firstLeg = flightRepository.findFirstLeg(origin, startOfDay, endOfDay);

        for(Flight first : firstLeg){
            Instant arrivalStart = first.getArrivalTime();
            Instant arrivalEnd = arrivalStart.plusSeconds(2 * 24 * 60 * 60); // plus 2 days
            List<Flight> secondLeg = flightRepository.findSecondLeg(first.getDestination(), arrivalStart, destination, arrivalEnd);

            for(Flight second : secondLeg){
                oneStopFlight.add(new OneStopFlightResponse(first,second));
            }
        }

        return new FlightRouteResponse(directFlight,oneStopFlight);
    }

    public Flight createFlight(FlightRequests flightRequest){
        Flight flight = Flight.builder()
                .flightNumber(flightRequest.getFlightNumber())
                .airlineName(flightRequest.getAirlineName())
                .departureTime(flightRequest.getDepartureTime())
                .arrivalTime(flightRequest.getArrivalTime())
                .origin(flightRequest.getOrigin())
                .destination(flightRequest.getDestination())
                .build();

        return flightRepository.save(flight);
    }

}
