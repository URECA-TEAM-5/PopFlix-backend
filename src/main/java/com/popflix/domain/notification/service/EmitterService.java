package com.popflix.domain.notification.service;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import java.util.Optional;

public interface EmitterService {
    SseEmitter save(Long userId, SseEmitter emitter);
    Optional<SseEmitter> get(Long userId);
    void remove(Long userId);
    boolean exists(Long userId);
}