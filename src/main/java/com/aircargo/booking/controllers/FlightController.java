package com.aircargo.booking.controllers;

import com.aircargo.booking.dto.FlightRequests;
import com.aircargo.booking.dto.FlightRouteResponse;
import com.aircargo.booking.entity.Flight;
import com.aircargo.booking.service.FlightService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/flight")
@RequiredArgsConstructor
public class FlightController {

    private final FlightService flightService;

    @GetMapping("/routes")
    public FlightRouteResponse getRoutes(
            @RequestParam String origin,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate departureDate
            ){

        return flightService.getRoutes(origin,destination,departureDate);
    }

    @PostMapping
    public Flight createFlight(@RequestBody FlightRequests request) {
        return flightService.createFlight(request);
    }
}
