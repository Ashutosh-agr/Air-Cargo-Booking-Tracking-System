package com.aircargo.booking.dto;

import com.aircargo.booking.model.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BookingHistoryResponse {
    private String refId;
    private String origin;
    private String destination;
    private int pieces;
    private int weightKg;
    private BookingStatus bookingStatus;
    private List<TimeLine> event;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TimeLine{
        private String eventType;
        private String location;
        private String flightNumber;
        private String timestamp;
    }
}
