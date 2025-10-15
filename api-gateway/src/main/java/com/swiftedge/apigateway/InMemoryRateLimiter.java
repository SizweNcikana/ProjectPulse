package com.swiftedge.apigateway;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.filter.ratelimit.RateLimiter;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@AllArgsConstructor
@RequiredArgsConstructor
public class InMemoryRateLimiter implements RateLimiter<Object> {

    private final Map<String, AtomicInteger> counts;
    private final int maxRequests;
    private final long windowMs;
    private long lastReset = System.currentTimeMillis();


    @Override
    public Mono<Response> isAllowed(String routeId, String key) {
        long now = System.currentTimeMillis();
        if (now - lastReset > windowMs) {
            counts.clear();
            lastReset = now;
        }

        counts.putIfAbsent(key, new AtomicInteger(0));
        int current = counts.get(key).incrementAndGet();

        boolean allowed = current <= maxRequests;

        return Mono.just(new Response(allowed, getHeaders(allowed)));

    }

    private Map<String, String> getHeaders(boolean allowed) {
        return Map.of("X-RateLimit-Remaining", allowed ? "1" : "0");
    }

    @Override
    public Map getConfig() {
        return Map.of();
    }

    @Override
    public Class getConfigClass() {
        return Object.class;
    }

    @Override
    public Object newConfig() {
        return null;
    }
}
