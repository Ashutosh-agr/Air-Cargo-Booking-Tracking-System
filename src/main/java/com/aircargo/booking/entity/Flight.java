package com.aircargo.booking.entity;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "flights")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Flight implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true,nullable = false)
    private String flightNumber;

    private String airlineName;

    @Column(name = "departure_time", columnDefinition = "TIMESTAMPTZ", nullable = false)
    private Instant departureTime;

    @Column(name = "arrival_time", columnDefinition = "TIMESTAMPTZ", nullable = false)
    private Instant arrivalTime;

    private String origin;
    private String destination;
}