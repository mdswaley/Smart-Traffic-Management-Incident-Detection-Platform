package com.mdswaley.traffic.smart_traffic_management.Analyzer;

import com.mdswaley.traffic.smart_traffic_management.Model.EmergencyVehicleType;
import com.mdswaley.traffic.smart_traffic_management.Model.TrafficPriority;
import org.springframework.stereotype.Component;

@Component
public class EmergencyVehicleAnalyzer {

    public TrafficPriority determinePriority(EmergencyVehicleType vehicleType) {

        if (vehicleType == null) {
            return TrafficPriority.NORMAL;
        }

        return switch (vehicleType) {

            case AMBULANCE -> TrafficPriority.EMERGENCY;

            case FIRE_TRUCK -> TrafficPriority.EMERGENCY;

            case POLICE -> TrafficPriority.EMERGENCY;

            case NONE -> TrafficPriority.NORMAL;
        };
    }
}
