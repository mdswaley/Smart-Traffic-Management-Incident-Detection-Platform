package com.mdswaley.traffic.smart_traffic_management.Analyzer;

import com.mdswaley.traffic.smart_traffic_management.Model.TrafficEvent;
import com.mdswaley.traffic.smart_traffic_management.Model.TrafficStatus;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class TrafficAnalyzer {

    public Mono<TrafficStatus> analyze(TrafficEvent event) {

        double score = calculateScore(event);

        String status = determineStatus(score);

        TrafficStatus status1 = new TrafficStatus(
                event.getIntersectionId(),
                status,
                score,
                event.getVehicleCount(),
                event.getWaitingVehicles(),
                event.getAverageSpeed()
        );

        return Mono.just(status1);
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
