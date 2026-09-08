package com.mdswaley.traffic.smart_traffic_management.Service;

import com.mdswaley.traffic.smart_traffic_management.Model.TrafficEvent;
import reactor.core.publisher.Mono;

public interface TrafficService {
    Mono<TrafficEvent> processEvent(TrafficEvent event);
}
