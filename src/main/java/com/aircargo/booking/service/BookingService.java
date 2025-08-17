package com.aircargo.booking.service;

import com.aircargo.booking.dto.BookingHistoryResponse;
import com.aircargo.booking.dto.BookingRequest;
import com.aircargo.booking.entity.Booking;
import com.aircargo.booking.entity.BookingEvents;
import com.aircargo.booking.entity.Flight;
import com.aircargo.booking.lock.WithDistributedLock;
import com.aircargo.booking.model.BookingStatus;
import com.aircargo.booking.model.EventType;
import com.aircargo.booking.repository.BookingEventRepository;
import com.aircargo.booking.repository.BookingRepository;
import com.aircargo.booking.repository.FlightRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final BookingEventRepository bookingEventRepository;
    private final FlightRepository flightRepository;

    @Transactional
    public Booking createBooking(BookingRequest bookingRequest){

        Optional<Booking> existing = bookingRepository.findByRefId(bookingRequest.getRefId());
        if(existing.isPresent()) return existing.get();

        Booking booking = Booking.builder()
                .refId(bookingRequest.getRefId())
                .origin(bookingRequest.getOrigin())
                .pieces(bookingRequest.getPieces())
                .weightKg(bookingRequest.getWeightKg())
                .destination(bookingRequest.getDestination())
                .status(BookingStatus.BOOKED)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        bookingRepository.save(booking);
        addEvent(booking,EventType.BOOKED,bookingRequest.getOrigin(),null);

        return booking;
    }

    @Transactional
    @Retryable(
            retryFor = { org.springframework.orm.ObjectOptimisticLockingFailureException.class },
            maxAttempts = 3,
            backoff = @Backoff(delay = 100, multiplier = 2.0, maxDelay = 800)
    )
    @WithDistributedLock(key = "'booking:' + #refId")
    public void departBooking(String refId, String flightNumber){
        Booking booking = bookingRepository.findByRefId(refId)
                .orElseThrow(() -> new RuntimeException("Booking not Found"));

        if(booking.getStatus() == BookingStatus.CANCELLED){
            throw new RuntimeException("Cannot depart a cancel booking after arrival");
        }

        booking.setStatus(BookingStatus.DEPARTED);
        booking.setUpdatedAt(LocalDateTime.now());

        Flight flight = flightRepository.findByFlightNumber(flightNumber);
        if (flight == null) {
            throw new RuntimeException("Flight not Found");
        }

        addEvent(booking,EventType.DEPART,flight.getOrigin(),flight);
    }

    @Transactional
    @Retryable(retryFor = { org.springframework.orm.ObjectOptimisticLockingFailureException.class }, maxAttempts = 3)
    @WithDistributedLock(key = "'booking:' + #refId")
    public void arriveBooking(String refId,String flightNumber){
        Booking booking = bookingRepository.findByRefId(refId)
                .orElseThrow(() -> new RuntimeException("Booking not Found"));

        if(booking.getStatus() == BookingStatus.CANCELLED){
            throw new RuntimeException("Cannot arrive a cancel booking after arrival");
        }

        booking.setStatus(BookingStatus.ARRIVED);
        booking.setUpdatedAt(LocalDateTime.now());

        Flight flight = flightRepository.findByFlightNumber(flightNumber);
        if (flight == null) {
            throw new RuntimeException("Flight not Found");
        }

        addEvent(booking,EventType.ARRIVE,flight.getDestination(),flight);
    }

    @Transactional
    @Retryable(retryFor = { org.springframework.orm.ObjectOptimisticLockingFailureException.class }, maxAttempts = 3)
    @WithDistributedLock(key = "'booking:' + #refId")
    public void cancelBooking(String refId){
        Booking booking = bookingRepository.findByRefId(refId)
                .orElseThrow(() -> new RuntimeException("Booking not Found"));

        if(booking.getStatus() == BookingStatus.ARRIVED){
            throw new RuntimeException("Cannot cancel booking after arrival");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        booking.setUpdatedAt(LocalDateTime.now());
        addEvent(booking,EventType.CANCEL, booking.getOrigin(),null);
    }

    @Transactional(readOnly = true)
    public BookingHistoryResponse getBookingHistory(String refId){
        Booking booking = bookingRepository.findByRefId(refId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        List<BookingEvents> events = bookingEventRepository.findByBookingIdOrderByTime(booking.getId());

        BookingHistoryResponse bookingHistoryResponse = new BookingHistoryResponse();
        bookingHistoryResponse.setRefId(booking.getRefId());
        bookingHistoryResponse.setOrigin(booking.getOrigin());
        bookingHistoryResponse.setDestination(booking.getDestination());
        bookingHistoryResponse.setPieces(booking.getPieces());
        bookingHistoryResponse.setWeightKg(booking.getWeightKg());
        bookingHistoryResponse.setBookingStatus(booking.getStatus());

        bookingHistoryResponse.setEvent(events.stream().map(e -> {
            BookingHistoryResponse.TimeLine t = new BookingHistoryResponse.TimeLine();
            t.setEventType(e.getEventType().name());
            t.setLocation(e.getLocation());
            t.setFlightNumber((e.getFlight() != null) ? e.getFlight().getFlightNumber() : null);
            t.setTimestamp(e.getTimestamp().toString());
            return t;
        }).toList());

        return bookingHistoryResponse;
    }

    private void addEvent(Booking booking, EventType eventType, String location, Flight flight){
        BookingEvents bookingEvents = BookingEvents.builder()
                .booking(booking)
                .eventType(eventType)
                .location(location)
                .flight(flight)
                .timestamp(LocalDateTime.now())
                .build();

        bookingEventRepository.save(bookingEvents);
    }
}
