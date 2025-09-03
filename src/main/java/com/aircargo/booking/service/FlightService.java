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
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@RequiredArgsConstructor
public class FlightService {

    private final FlightRepository flightRepository;

    @Cacheable(cacheNames = "routeCache", key = "T(java.util.Objects).hash(#origin, #destination, #departureDate)")
    public FlightRouteResponse getRoutes(String origin, String destination, LocalDate departureDate){
        Instant startOfDay = departureDate.atStartOfDay(ZoneOffset.UTC).toInstant();
        Instant endOfDay = departureDate.plusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant();
        List<Flight> directFlight = flightRepository.findDirectFlight(origin, startOfDay, endOfDay, destination);

        Instant maxConnectionTime = departureDate.plusDays(2).atStartOfDay(ZoneOffset.UTC).toInstant();

        List<Object[]> results = flightRepository.findOneStopFlights(
                origin, destination, startOfDay, endOfDay, maxConnectionTime
        );

        List<OneStopFlightResponse> oneStopFlights = new ArrayList<>();

        for (Object[] row : results) {
            Flight firstFlight = (Flight) row[0];
            Flight secondFlight = (Flight) row[1];
            oneStopFlights.add(new OneStopFlightResponse(firstFlight, secondFlight));
        }

        return new FlightRouteResponse(directFlight,oneStopFlights);
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

    public String checkFlight(String origin, String destination, String flightNumber){
        Flight flight = flightRepository.findByFlightNumber(flightNumber);

        if(!Objects.equals(flight.getOrigin(), origin) && !Objects.equals(flight.getDestination(),destination)){
            return "No Flight in DB";
        }

        if(Objects.equals(flight.getOrigin(), origin) && Objects.equals(flight.getDestination(),destination)){
            return "Direct flight";
        }else{
            return "One Stop";
        }
    }

    public HashSet<String> possibleFlights(Flight flight,String destination,String origin){
        Instant startOfDay = flight.getArrivalTime();
        Instant endOfDay = startOfDay.plus(1, ChronoUnit.DAYS);
        Instant maxConnectionTime = startOfDay.plus(2,ChronoUnit.DAYS);
        List<String> flightName = new ArrayList<>();
        List<Object[]> res = flightRepository.findOneStopFlightsNative(origin,destination,startOfDay,endOfDay,maxConnectionTime);

        for(Object[] o: res){
            String a1 = (String) o[0];
            String a2 = (String) o[1];
            if(a1.equals(flight.getFlightNumber())){
                flightName.add(a2);
            }
        }

        return new HashSet<>(flightName);
    }

}
