package com.mdswaley.traffic.smart_traffic_management.Model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TrafficEvent {
    private String intersectionId;

    private String roadId;

    private int vehicleCount;

    private double averageSpeed;

    private int waitingVehicles;

    private LocalDateTime timestamp;
}
