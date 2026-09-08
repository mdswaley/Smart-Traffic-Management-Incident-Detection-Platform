package com.mdswaley.traffic.smart_traffic_management.Repository;

import com.mdswaley.traffic.smart_traffic_management.Model.TrafficEvent;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface TrafficEventRepository extends ReactiveMongoRepository<TrafficEvent, String> {
    Flux<TrafficEvent> findByIntersectionId(String intersectionId);
}
