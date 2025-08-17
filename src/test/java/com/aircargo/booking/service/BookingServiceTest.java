package com.aircargo.booking.service;

import com.aircargo.booking.entity.Booking;
import com.aircargo.booking.model.BookingStatus;
import com.aircargo.booking.repository.BookingRepository;
import com.aircargo.booking.repository.FlightRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {

    @Mock
    BookingRepository bookingRepository;
    @Mock
    FlightRepository flightRepository;

    @InjectMocks
    BookingService bookingService;

    @Test
    void departBooking_shouldThrowIfBookingNotFound() {
        when(bookingRepository.findByRefId("ref1")).thenReturn(Optional.empty());
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                bookingService.departBooking("ref1", "FL123")
        );
        assertEquals("Booking not Found", ex.getMessage());
    }

    @Test
    void departBooking_shouldThrowIfFlightNotFound() {
        Booking booking = Booking.builder().status(BookingStatus.BOOKED).build();
        when(bookingRepository.findByRefId("ref1")).thenReturn(Optional.of(booking));
        when(flightRepository.findByFlightNumber("FL123")).thenReturn(null);
        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                bookingService.departBooking("ref1", "FL123")
        );
        assertEquals("Flight not Found", ex.getMessage());
    }
}
