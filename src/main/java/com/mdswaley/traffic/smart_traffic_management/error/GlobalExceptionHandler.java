package com.mdswaley.traffic.smart_traffic_management.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(TrafficDataNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleTrafficDataNotFound(TrafficDataNotFoundException ex) {

        return Map.of("error", "TRAFFIC_DATA_NOT_FOUND", "message", ex.getMessage());
    }
}
