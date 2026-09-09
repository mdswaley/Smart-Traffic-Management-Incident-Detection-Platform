package com.mdswaley.traffic.smart_traffic_management.Analyzer;

import com.mdswaley.traffic.smart_traffic_management.Model.TrafficEvent;
import com.mdswaley.traffic.smart_traffic_management.Model.TrafficStatus;
import org.springframework.stereotype.Component;

@Component
public class TrafficAnalyzer {

    public TrafficStatus analyze(TrafficEvent event) {

        double score = calculateScore(event);

        String status = determineStatus(score);

        return new TrafficStatus(
                event.getIntersectionId(),
                status,
                score,
                event.getVehicleCount(),
                event.getWaitingVehicles(),
                event.getAverageSpeed()
        );
    }

    private double calculateScore(TrafficEvent event) {

        double vehicleScore = Math.min(event.getVehicleCount(), 100);

        double waitingScore = Math.min(event.getWaitingVehicles(), 100);

        double speedScore = Math.max(0, 100 - (event.getAverageSpeed() * 5));

        return (vehicleScore * 0.4) + (waitingScore * 0.4) + (speedScore * 0.2);
    }

    private String determineStatus(double score) {

        if (score < 25) {
            return "LOW";
        }

        if (score < 50) {
            return "MEDIUM";
        }

        if (score < 75) {
            return "HIGH";
        }

        return "CRITICAL";
    }
}
