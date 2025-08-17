package com.aircargo.booking.dto;

import com.aircargo.booking.entity.Flight;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlightRouteResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -4479771871107458093L;

    public List<Flight> direct;
    public List<OneStopFlightResponse> oneStop;
}
