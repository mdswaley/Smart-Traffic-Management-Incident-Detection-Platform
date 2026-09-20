package com.mdswaley.traffic.smart_traffic_management.Model;

public enum TrafficPriority {
    NORMAL(1),
    HIGH(2),
    EMERGENCY(3),
    AMBULANCE(4);

    private final int level;

    TrafficPriority(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
