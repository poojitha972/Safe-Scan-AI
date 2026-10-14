
package com.safescan.safescan.controller;

import com.safescan.safescan.ScanHistory;
import com.safescan.safescan.ScanHistoryRepository;
import com.safescan.safescan.User;
import com.safescan.safescan.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class AdminController {

    private final UserRepository userRepository;
    private final ScanHistoryRepository historyRepository;

    public AdminController(
            UserRepository userRepository,
            ScanHistoryRepository historyRepository) {
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
    }

    @GetMapping("/admin")
    public String adminDashboard(HttpSession session, Model model) {
        Object adminId = session.getAttribute("adminUserId");

        if (!(adminId instanceof Long)) {
            return "redirect:/admin/login";
        }

        User admin = userRepository.findById((Long) adminId)
                .orElse(null);

        if (admin == null || !"ADMIN".equals(admin.getRole())) {
            session.removeAttribute("adminUserId");
            return "redirect:/admin/login";
        }

        List<ScanHistory> recentScans =
                historyRepository.findTop10ByOrderByScannedAtDesc();

        model.addAttribute("adminName", admin.getName());
        model.addAttribute("totalUsers", userRepository.count());
        model.addAttribute("totalScans", historyRepository.count());
        model.addAttribute("safeScans",
                historyRepository.countByCategoryIgnoreCase("Safe"));
        model.addAttribute("suspiciousScans",
                historyRepository.countByCategoryIgnoreCase("Suspicious"));
        model.addAttribute("highRiskScans",
                historyRepository.countByCategoryIgnoreCase("High Risk"));
        model.addAttribute("recentScans", recentScans);

        return "admin-dashboard";
    }
}


