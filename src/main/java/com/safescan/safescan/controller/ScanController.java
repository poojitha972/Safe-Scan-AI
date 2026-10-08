package com.safescan.safescan.controller;

import com.safescan.safescan.service.ScanService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class ScanController {

    private final ScanService scanService;

    public ScanController(ScanService scanService) {
        this.scanService = scanService;
    }

    @PostMapping("/scan")
    public Map<String, Object> scan(@RequestBody Map<String, String> request) {

        String input = request.get("input");

        return scanService.analyze(input);
    }
}