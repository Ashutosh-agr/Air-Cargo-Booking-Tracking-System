package com.aircargo.booking.dto;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequest {
    public String refId;
    public String origin;
    public String destination;
    public int pieces;
    public int weightKg;
}
