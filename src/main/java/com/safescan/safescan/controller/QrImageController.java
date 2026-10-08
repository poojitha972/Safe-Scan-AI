package com.safescan.safescan.controller;

import com.safescan.safescan.service.QrService;
import com.safescan.safescan.service.ScanService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/qr")
public class QrImageController {

    private final QrService qrService;
    private final ScanService scanService;

    public QrImageController(QrService qrService, ScanService scanService) {
        this.qrService = qrService;
        this.scanService = scanService;
    }

    @PostMapping("/image")
    public ResponseEntity<?> scanQrImage(
            @RequestParam("file") MultipartFile file) {

        try {
            String decodedText = qrService.decodeQr(file);

            Map<String, Object> analysis =
                    scanService.analyze(decodedText);

            analysis.put("decodedText", decodedText);

            return ResponseEntity.ok(analysis);

        } catch (Exception e) {

            return ResponseEntity.badRequest().body(
                    Map.of(
                            "error",
                            "Could not read the QR code. Please upload a clear QR image."
                    )
            );
        }
    }
}