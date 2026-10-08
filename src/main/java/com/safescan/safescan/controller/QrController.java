package com.safescan.safescan.controller;

import com.safescan.safescan.service.ScanService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/qr")
public class QrController {

    private final ScanService scanService;

    public QrController(ScanService scanService) {
        this.scanService = scanService;
    }

    @PostMapping("/scan")
    public Map<String, Object> scanQr(@RequestBody Map<String, String> request) {

        String input = request.get("input");

        return scanService.analyze(input);
    }
}
