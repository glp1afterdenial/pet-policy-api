package com.thepointspup.petpolicyapi.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ViewsServiceTest {

    private ViewsService service;

    @BeforeEach
    void setUp() {
        service = new ViewsService();
    }

    @Test
    void incrementAndGet_startsAtOne() {
        assertEquals(1, service.incrementAndGet());
    }

    @Test
    void incrementAndGet_incrementsEachCall() {
        service.incrementAndGet();
        service.incrementAndGet();
        assertEquals(3, service.incrementAndGet());
    }

    @Test
    void incrementAndGet_isThreadSafe() throws InterruptedException {
        int threads = 10;
        int incrementsPerThread = 100;
        Thread[] workers = new Thread[threads];

        for (int i = 0; i < threads; i++) {
            workers[i] = new Thread(() -> {
                for (int j = 0; j < incrementsPerThread; j++) {
                    service.incrementAndGet();
                }
            });
            workers[i].start();
        }

        for (Thread worker : workers) {
            worker.join();
        }

        assertEquals(threads * incrementsPerThread + 1, service.incrementAndGet());
    }
}
