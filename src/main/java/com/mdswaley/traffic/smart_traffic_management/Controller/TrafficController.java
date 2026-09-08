package com.mdswaley.traffic.smart_traffic_management.Controller;

import com.mdswaley.traffic.smart_traffic_management.Model.TrafficEvent;
import com.mdswaley.traffic.smart_traffic_management.Service.TrafficService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/traffic")
@RequiredArgsConstructor
public class TrafficController {

    private final TrafficService trafficService;

    @PostMapping("/events")
    public Mono<TrafficEvent> createEvents(@RequestBody TrafficEvent event) {
        return trafficService.saveEvent(event);
    }

    @GetMapping("/intersections/{id}/events")
    public Flux<TrafficEvent> getEvents(@PathVariable String id) {
        return trafficService.getEvents(id);
    }
}
