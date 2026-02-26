package com.thepointspup.petpolicyapi.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ViewsService {

    private static final Path DATA_FILE = Path.of("data/views.json");
    private final AtomicLong count = new AtomicLong(0);
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        try {
            if (Files.exists(DATA_FILE)) {
                Map<?, ?> data = objectMapper.readValue(DATA_FILE.toFile(), Map.class);
                Object views = data.get("views");
                if (views instanceof Number) {
                    count.set(((Number) views).longValue());
                }
            }
        } catch (IOException e) {
            // Start from 0 if file is missing or corrupt
        }
    }

    public long incrementAndGet() {
        long current = count.incrementAndGet();
        persist(current);
        return current;
    }

    private void persist(long value) {
        try {
            Files.createDirectories(DATA_FILE.getParent());
            objectMapper.writeValue(DATA_FILE.toFile(), Map.of("views", value));
        } catch (IOException e) {
            // Non-fatal — count stays in memory
        }
    }
}
