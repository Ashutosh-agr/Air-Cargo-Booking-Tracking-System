-- ===============================
--  Create Booking table
-- ===============================
CREATE TABLE booking (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    ref_id VARCHAR(255) NOT NULL UNIQUE,
    origin VARCHAR(255),
    destination VARCHAR(255),
    pieces INT,
    weight_kg INT,
    status VARCHAR(50), -- Enum BookingStatus stored as string
    created_at TIMESTAMP,
    updated_at TIMESTAMP,
    version BIGINT NOT NULL
);

-- ===============================
--  Create Flights table
-- ===============================
CREATE TABLE flights (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    flight_number VARCHAR(255) NOT NULL UNIQUE,
    airline_name VARCHAR(255),
    departure_time TIMESTAMPTZ NOT NULL,
    arrival_time TIMESTAMPTZ NOT NULL,
    origin VARCHAR(50),
    destination VARCHAR(50)
);

-- ===============================
--  Create Booking Events table
-- ===============================
CREATE TABLE booking_events (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    booking_id UUID NOT NULL,
    event_type VARCHAR(50), -- Enum EventType stored as string
    location VARCHAR(255),
    flight_id UUID,
    timestamp TIMESTAMP,

    CONSTRAINT fk_booking FOREIGN KEY (booking_id) REFERENCES booking (id) ON DELETE CASCADE,
    CONSTRAINT fk_flight FOREIGN KEY (flight_id) REFERENCES flights (id) ON DELETE SET NULL
);
