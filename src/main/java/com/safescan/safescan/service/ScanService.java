package com.safescan.safescan.service;

import org.springframework.stereotype.Service;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class ScanService {

    public Map<String, Object> analyze(String input) {

        int riskScore = 0;
        StringBuilder reasons = new StringBuilder();

        if (input == null || input.trim().isEmpty()) {
            return createResult(
                    0,
                    "Safe",
                    "No content was provided for analysis.",
                    "Enter a URL or message to scan."
            );
        }

        String text = input.toLowerCase().trim();

        // =========================================
        // 1. URL SECURITY CHECKS
        // =========================================

        if (text.contains("http://")) {
            riskScore += 15;
            reasons.append("Uses an unsecured HTTP connection. ");
        }

        if (text.contains("@")) {
            riskScore += 20;
            reasons.append(
                    "Contains an @ symbol, which may hide the real destination. "
            );
        }

        // =========================================
        // 2. URL SHORTENER CHECK
        // =========================================

        if (text.contains("bit.ly")
                || text.contains("tinyurl")
                || text.contains("t.co")
                || text.contains("shorturl")
                || text.contains("is.gd")
                || text.contains("cutt.ly")) {

            riskScore += 20;
            reasons.append(
                    "Uses a URL shortening service that can hide the final destination. "
            );
        }

        // =========================================
        // 3. SUSPICIOUS URL KEYWORDS
        // =========================================

        if (text.contains("login")
                || text.contains("log-in")
                || text.contains("verify")
                || text.contains("verification")
                || text.contains("account")
                || text.contains("password")
                || text.contains("signin")
                || text.contains("sign-in")) {

            riskScore += 15;
            reasons.append(
                    "Contains words commonly associated with phishing attempts. "
            );
        }

        // =========================================
        // 4. SUSPICIOUS DOMAIN EXTENSIONS
        // =========================================

        if (text.contains(".xyz")
                || text.contains(".tk")
                || text.contains(".top")
                || text.contains(".click")
                || text.contains(".work")
                || text.contains(".gq")
                || text.contains(".ml")) {

            riskScore += 20;
            reasons.append(
                    "Uses a domain extension that may be associated with suspicious websites. "
            );
        }

        // =========================================
        // 5. URGENCY / PRESSURE LANGUAGE
        // =========================================

        if (text.contains("urgent")
                || text.contains("immediately")
                || text.contains("act now")
                || text.contains("last chance")
                || text.contains("within 24 hours")
                || text.contains("expires today")) {

            riskScore += 15;
            reasons.append(
                    "Uses urgent or pressure-based language. "
            );
        }

        // =========================================
        // 6. SENSITIVE INFORMATION REQUEST
        // =========================================

        if (text.contains("otp")
                || text.contains("one time password")
                || text.contains("password")
                || text.contains("pin")
                || text.contains("cvv")
                || text.contains("card number")
                || text.contains("bank details")) {

            riskScore += 20;
            reasons.append(
                    "Requests sensitive personal or financial information. "
            );
        }

        // =========================================
        // 7. PRIZE / LOTTERY SCAM CHECK
        // =========================================

        if (text.contains("winner")
                || text.contains("congratulations")
                || text.contains("prize")
                || text.contains("lottery")
                || text.contains("reward")
                || text.contains("cash prize")
                || text.contains("you have won")) {

            riskScore += 15;
            reasons.append(
                    "Contains possible prize, lottery, or reward scam indicators. "
            );
        }

        // =========================================
        // 8. ACCOUNT THREAT CHECK
        // =========================================

        if (text.contains("account blocked")
                || text.contains("account suspended")
                || text.contains("account will be closed")
                || text.contains("verify your account")
                || text.contains("account compromised")) {

            riskScore += 15;
            reasons.append(
                    "Uses account verification or account suspension pressure. "
            );
        }

        // =========================================
        // 9. FINANCIAL SCAM CHECK
        // =========================================

        if (text.contains("refund")
                || text.contains("cashback")
                || text.contains("loan approved")
                || text.contains("investment")
                || text.contains("double your money")
                || text.contains("bank")) {

            riskScore += 10;
            reasons.append(
                    "Contains financial or money-related scam indicators. "
            );
        }

        // =========================================
        // 10. IMPERSONATION CHECK
        // =========================================

        if (text.contains("customer care")
                || text.contains("support team")
                || text.contains("official team")
                || text.contains("security team")
                || text.contains("government")) {

            riskScore += 10;
            reasons.append(
                    "May be attempting to impersonate an organization or support team. "
            );
        }

        // =========================================
        // ML MODEL ANALYSIS
        // =========================================

        try {

            RestTemplate restTemplate = new RestTemplate();

            Map<String, String> request = new HashMap<>();
            request.put("message", input);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> entity =
                    new HttpEntity<>(request, headers);

            ResponseEntity<Map> response =
                    restTemplate.postForEntity(
                            "http://127.0.0.1:5000/predict",
                            entity,
                            Map.class
                    );

            if (response.getBody() != null) {

                Object mlRiskObject =
                        response.getBody().get("riskScore");

                if (mlRiskObject instanceof Number) {

                    int mlRiskScore =
                            (int) Math.round(
                                    ((Number) mlRiskObject).doubleValue()
                            );

                    // Use the higher score from ML or security checks
                    if (mlRiskScore > riskScore) {
                        riskScore = mlRiskScore;
                    }

                    reasons.append(
                            "Machine learning analysis was also performed. "
                    );
                }
            }

        } catch (Exception e) {

            // If Python ML service is unavailable,
            // continue using the existing security checks.
            reasons.append(
                    "ML service was unavailable, so security rule analysis was used. "
            );
        }

        // =========================================
        // LIMIT SCORE TO 100
        // =========================================

        if (riskScore > 100) {
            riskScore = 100;
        }

        // =========================================
        // CLASSIFY RISK
        // =========================================

        String category;

        if (riskScore >= 60) {
            category = "High Risk";
        } else if (riskScore >= 30) {
            category = "Suspicious";
        } else {
            category = "Safe";
        }

        // =========================================
        // NO SUSPICIOUS INDICATORS
        // =========================================

        if (reasons.length() == 0) {
            reasons.append(
                    "No major suspicious indicators were detected."
            );
        }

        // =========================================
        // RECOMMENDATION
        // =========================================

        String recommendation;

        if (riskScore >= 60) {

            recommendation =
                    "Do not click the link or share personal information.";

        } else if (riskScore >= 30) {

            recommendation =
                    "Be careful and verify the source before taking action.";

        } else {

            recommendation =
                    "The content appears relatively safe, but always remain cautious.";
        }

        return createResult(
                riskScore,
                category,
                reasons.toString(),
                recommendation
        );
    }

    // =============================================
    // CREATE RESPONSE
    // =============================================

    private Map<String, Object> createResult(
            int riskScore,
            String category,
            String reasons,
            String recommendation) {

        Map<String, Object> result = new HashMap<>();

        result.put("riskScore", riskScore);
        result.put("category", category);
        result.put("reasons", reasons);
        result.put("recommendation", recommendation);

        return result;
    }
}