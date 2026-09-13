package com.mdswaley.traffic.smart_traffic_management.Service;

import com.mdswaley.traffic.smart_traffic_management.Analyzer.TrafficAnalyzer;
import com.mdswaley.traffic.smart_traffic_management.Model.TrafficEvent;
import com.mdswaley.traffic.smart_traffic_management.Model.TrafficStatus;
import com.mdswaley.traffic.smart_traffic_management.Repository.TrafficEventRepository;
import com.mdswaley.traffic.smart_traffic_management.error.TrafficDataNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TrafficServiceImp implements TrafficService{

    private final TrafficEventRepository repository;
    private final TrafficAnalyzer trafficAnalyzer;

    @Override
    public Mono<TrafficEvent> saveEvent(TrafficEvent event) {

        event.setTimestamp(LocalDateTime.now());

        return repository.save(event);
    }

    @Override
    public Flux<TrafficEvent> getEvents(String intersectionId) {
        return repository.findByIntersectionId(intersectionId)
                .switchIfEmpty(Mono.error(new TrafficDataNotFoundException("No traffic data found for intersection: "+intersectionId)));
    }

    @Override
    public Mono<TrafficStatus> getTrafficStatus(String intersectionId) {

        return repository
                .findByIntersectionId(intersectionId)
                .filter(event -> event.getVehicleCount() >= 0
                                && event.getWaitingVehicles() >= 0
                                && event.getAverageSpeed() >= 0
                )
                .sort((event1, event2) ->
                        event2.getTimestamp().compareTo(event1.getTimestamp()))
                .next()
                .flatMap(trafficAnalyzer::analyze)
                .switchIfEmpty(Mono.error(new TrafficDataNotFoundException(
                        "No traffic data found for intersection: " + intersectionId)));
    }

    @Override
    public Flux<TrafficStatus> liveTraffic(String intersectionId) {
        return Flux.interval(Duration.ofSeconds(5))
                .flatMap(tick -> repository
                        .findByIntersectionId(intersectionId)
                        .sort((event1, event2) -> event2.getTimestamp()
                                .compareTo(event1.getTimestamp()))
                .next()
                .flatMap(trafficAnalyzer::analyze));
    }
}
