package com.aircargo.booking.entity;

import com.aircargo.booking.model.BookingStatus;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "booking")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Booking {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(unique = true,nullable = false)
    private String refId;

    private String origin;
    private String destination;
    private int pieces;
    private int weightKg;

    @Enumerated(EnumType.STRING)
    private BookingStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @Version
    private long version;
}
