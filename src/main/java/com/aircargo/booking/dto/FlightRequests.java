package com.aircargo.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class  FlightRequests {
    public String flightNumber;
    public String airlineName;
    public Instant departureTime;
    public Instant arrivalTime;
    public String origin;
    public String destination;
}
