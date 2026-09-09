package com.mdswaley.traffic.smart_traffic_management.Service;

import com.mdswaley.traffic.smart_traffic_management.Model.TrafficEvent;
import com.mdswaley.traffic.smart_traffic_management.Model.TrafficStatus;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface TrafficService {

    Mono<TrafficEvent> saveEvent(TrafficEvent event);

    Flux<TrafficEvent> getEvents(String intersectionId);

    Mono<TrafficStatus> getTrafficStatus(String intersectionId);
}
