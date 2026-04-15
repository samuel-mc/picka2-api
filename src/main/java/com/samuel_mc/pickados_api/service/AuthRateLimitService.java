package com.samuel_mc.pickados_api.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthRateLimitService {

    private static final int MAX_FAILED_ATTEMPTS = 8;
    private static final Duration WINDOW = Duration.ofMinutes(15);

    private final ConcurrentHashMap<String, AttemptWindow> failedLogins = new ConcurrentHashMap<>();

    public boolean isLoginBlocked(String key) {
        AttemptWindow window = failedLogins.get(key);
        if (window == null) {
            return false;
        }
        if (window.isExpired()) {
            failedLogins.remove(key);
            return false;
        }
        return window.failures >= MAX_FAILED_ATTEMPTS;
    }

    public void recordLoginFailure(String key) {
        failedLogins.compute(key, (ignored, current) -> {
            if (current == null || current.isExpired()) {
                return new AttemptWindow(1, Instant.now());
            }
            current.failures++;
            return current;
        });
    }

    public void clearLoginFailures(String key) {
        failedLogins.remove(key);
    }

    private static final class AttemptWindow {
        private int failures;
        private final Instant startedAt;

        private AttemptWindow(int failures, Instant startedAt) {
            this.failures = failures;
            this.startedAt = startedAt;
        }

        private boolean isExpired() {
            return Instant.now().isAfter(startedAt.plus(WINDOW));
        }
    }
}
