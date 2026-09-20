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

        // ---------------------------------------------------------
        // 1. VEHICLE SCORE
        // ---------------------------------------------------------
        // We take the number of vehicles and cap it at 100.
        //
        // Example:
        //   20 vehicles  -> 20
        //   70 vehicles  -> 70
        //   100 vehicles -> 100
        //   150 vehicles -> 100
        //
        // Why cap at 100?
        // Because we want all three factors to approximately
        // operate on a 0-100 scale.
        double vehicleScore = Math.min(event.getVehicleCount(), 100);


        // ---------------------------------------------------------
        // 2. WAITING VEHICLE SCORE
        // ---------------------------------------------------------
        // Same idea as vehicleScore.
        //
        // We consider 100 waiting vehicles as the maximum
        // contribution to this component.
        //
        // Example:
        //   10 waiting vehicles  -> 10
        //   60 waiting vehicles  -> 60
        //   100 waiting vehicles -> 100
        //   150 waiting vehicles -> 100
        double waitingScore = Math.min(event.getWaitingVehicles(), 100);


        // ---------------------------------------------------------
        // 3. SPEED SCORE
        // ---------------------------------------------------------
        // Here we convert average speed into a congestion score.
        //
        // Higher speed generally means less congestion,
        // so speed has an INVERSE relationship with congestion.
        //
        // The formula:
        //
        //     100 - (averageSpeed * 5)
        //
        // means:
        //
        //     speed = 0 km/h   -> 100
        //     speed = 5 km/h   -> 75
        //     speed = 10 km/h  -> 50
        //     speed = 15 km/h  -> 25
        //     speed = 20 km/h  -> 0
        //
        // Why * 5?
        // Because we decided that every 1 km/h increase in speed
        // decreases the congestion contribution by 5 points.
        //
        // IMPORTANT:
        // The number 5 is a design assumption for our demo.
        // It is NOT a universal traffic engineering constant.
        //
        // Math.max(0, ...) prevents the score from becoming negative.
        //
        // Example:
        //     speed = 30
        //
        //     100 - (30 * 5)
        //     = 100 - 150
        //     = -50
        //
        // We don't want -50, so Math.max() gives us 0.
        double speedScore = Math.max(0, 100 - (event.getAverageSpeed() * 5));


        // ---------------------------------------------------------
        // 4. COMBINE THE THREE SCORES
        // ---------------------------------------------------------
        //
        // Vehicle score     -> 40% importance
        // Waiting score     -> 40% importance
        // Speed score       -> 20% importance
        //
        // 0.4 = 40%
        // 0.4 = 40%
        // 0.2 = 20%
        //
        // Total:
        //
        //     0.4 + 0.4 + 0.2 = 1.0
        //
        // Therefore the final score remains approximately
        // within the 0-100 range.
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
