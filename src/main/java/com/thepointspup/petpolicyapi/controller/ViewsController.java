package com.thepointspup.petpolicyapi.controller;

import com.thepointspup.petpolicyapi.service.ViewsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
@Tag(name = "Views", description = "Site visitor counter")
public class ViewsController {

    private final ViewsService viewsService;

    public ViewsController(ViewsService viewsService) {
        this.viewsService = viewsService;
    }

    @GetMapping("/views")
    @Operation(summary = "Get and increment view count", description = "Increments the visitor count and returns the current total")
    public Map<String, Long> getViews() {
        return Map.of("views", viewsService.incrementAndGet());
    }
}
