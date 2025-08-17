package com.aircargo.booking.controllers;

import com.aircargo.booking.dto.BookingHistoryResponse;
import com.aircargo.booking.dto.BookingRequest;
import com.aircargo.booking.entity.Booking;
import com.aircargo.booking.service.BookingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public Booking createBooking(@RequestBody BookingRequest bookingRequest){
        return bookingService.createBooking(bookingRequest);
    }

    @PatchMapping("/{refId}/depart")
    public void departBooking(@PathVariable String refId, @RequestParam String flightNumber){
        bookingService.departBooking(refId,flightNumber);
    }

    @PatchMapping("/{refId}/arrive")
    public void arriveBooming(@PathVariable String refId,@RequestParam String flightNumber){
        bookingService.arriveBooking(refId,flightNumber);
    }

    @PatchMapping("/{refId}/cancel")
    public void cancelBooking(@PathVariable String refId){
        bookingService.cancelBooking(refId);
    }

    @GetMapping("/{refId}")
    public BookingHistoryResponse getBookingHistory(@PathVariable String refId){
        return bookingService.getBookingHistory(refId);
    }
}
