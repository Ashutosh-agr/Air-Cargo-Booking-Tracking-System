ALTER TABLE booking ADD CONSTRAINT uq_booking_refid UNIQUE (ref_id);

-- Flights: direct + first/second leg lookups
CREATE INDEX IF NOT EXISTS idx_flight_origin_departure
  ON flights (origin, departure_time);

CREATE INDEX IF NOT EXISTS idx_flight_destination_departure
  ON flights (destination, departure_time);

CREATE INDEX IF NOT EXISTS idx_flight_origin_dest_departure
  ON flights (origin, destination, departure_time);

-- Bookings: idempotent create + fast lookups
CREATE INDEX IF NOT EXISTS idx_booking_refid
  ON booking (ref_id);

-- Booking events: timeline reads
CREATE INDEX IF NOT EXISTS idx_booking_events_booking_ts
  ON booking_events (booking_id, timestamp);
