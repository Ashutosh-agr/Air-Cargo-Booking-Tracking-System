package com.aircargo.booking.dto;

import com.aircargo.booking.entity.Flight;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

@Data
@AllArgsConstructor
public class OneStopFlightResponse implements Serializable {
    @Serial
    private static final long serialVersionUID = -4479771871107458093L;

    private Flight firstLeg;
    private Flight secondLeg;
}