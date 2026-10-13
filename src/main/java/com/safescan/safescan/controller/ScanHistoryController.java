package com.safescan.safescan.controller;
import org.springframework.web.server.ResponseStatusException;

import com.safescan.safescan.ScanHistory;
import com.safescan.safescan.ScanHistoryRepository;
import com.safescan.safescan.User;
import com.safescan.safescan.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/history")
public class ScanHistoryController {

    private final ScanHistoryRepository historyRepository;
    private final UserRepository userRepository;

    public ScanHistoryController(
            ScanHistoryRepository historyRepository,
            UserRepository userRepository) {
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    private User getLoggedInUser(HttpSession session) {
        Object value = session.getAttribute("userId");

        if (!(value instanceof Long)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED, "Please log in first.");
        }

        return userRepository.findById((Long) value)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.UNAUTHORIZED, "User not found."));
    }

    @GetMapping
    public List<Map<String, Object>> getHistory(HttpSession session) {
        User user = getLoggedInUser(session);

        return historyRepository
                .findAllByUser_IdOrderByScannedAtDesc(user.getId())
                .stream()
                .map(scan -> {
                    Map<String, Object> item = new LinkedHashMap<>();
                    item.put("id", scan.getId());
                    item.put("type", scan.getType());
                    item.put("content", scan.getContent());
                    item.put("score", scan.getRiskScore());
                    item.put("category", scan.getCategory());
                    item.put("explanation", scan.getExplanation());
                    item.put("recommendation", scan.getRecommendation());
                    item.put("time", scan.getScannedAt());
                    return item;
                })
                .toList();
    }

    @PostMapping
    public Map<String, Object> saveHistory(
            @RequestBody Map<String, Object> request,
            HttpSession session) {

        User user = getLoggedInUser(session);

        ScanHistory scan = new ScanHistory();
        scan.setUser(user);
        scan.setType(String.valueOf(request.getOrDefault("type", "url")));
        scan.setContent(String.valueOf(request.getOrDefault("content", "")));
        scan.setRiskScore(toInteger(request.get("score")));
        scan.setCategory(String.valueOf(
                request.getOrDefault("category", "Unknown")));
        scan.setExplanation(String.valueOf(
                request.getOrDefault("explanation", "")));
        scan.setRecommendation(String.valueOf(
                request.getOrDefault("recommendation", "")));

        ScanHistory saved = historyRepository.save(scan);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", saved.getId());
        response.put("message", "Scan history saved.");
        return response;
    }

    @DeleteMapping
    public Map<String, String> clearHistory(HttpSession session) {
        User user = getLoggedInUser(session);
        historyRepository.deleteAllByUser_Id(user.getId());

        return Map.of("message", "Your scan history was cleared.");
    }

    private Integer toInteger(Object value) {
        if (value instanceof Number number) {
            return number.intValue();
        }

        try {
            return value == null ? null : Integer.parseInt(value.toString());
        } catch (NumberFormatException exception) {
            return null;
        }
    }
}

