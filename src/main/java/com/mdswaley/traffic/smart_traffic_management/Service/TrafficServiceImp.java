package com.mdswaley.traffic.smart_traffic_management.Service;

import com.mdswaley.traffic.smart_traffic_management.Model.TrafficEvent;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class TrafficServiceImp implements TrafficService{

    @Override
    public Mono<TrafficEvent> processEvent(TrafficEvent event) {
        return Mono.just(event);
    }
}
