package com.popflix.domain.notification.service.impl;

import com.popflix.domain.notification.service.EmitterService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class EmitterServiceImpl implements EmitterService {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public SseEmitter save(Long userId, SseEmitter emitter) {
        emitters.put(userId, emitter);
        log.info("SSE Emitter Created: {}", userId);
        return emitter;
    }

    @Override
    public Optional<SseEmitter> get(Long userId) {
        return Optional.ofNullable(emitters.get(userId));
    }

    @Override
    public void remove(Long userId) {
        emitters.remove(userId);
        log.info("SSE Emitter Removed: {}", userId);
    }

    @Override
    public boolean exists(Long userId) {
        return emitters.containsKey(userId);
    }
}