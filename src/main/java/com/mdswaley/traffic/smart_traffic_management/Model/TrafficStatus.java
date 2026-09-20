package com.mdswaley.traffic.smart_traffic_management.Model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class TrafficStatus {
    private String intersectionId;
    private String status;
    private double congestionScore;
    private int vehicleCount;
    private int waitingVehicles;
    private double averageSpeed;
    private String vehicleType;
    private TrafficPriority priority;
}
