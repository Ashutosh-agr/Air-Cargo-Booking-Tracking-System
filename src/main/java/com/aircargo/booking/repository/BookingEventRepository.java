package com.aircargo.booking.repository;

import com.aircargo.booking.entity.BookingEvents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface BookingEventRepository extends JpaRepository<BookingEvents, UUID> {

    @Query("Select be from BookingEvents as be where be.booking.id = :bookingId order by be.timestamp")
    List<BookingEvents> findByBookingIdOrderByTime(@Param("bookingId") UUID bookingId);
}
