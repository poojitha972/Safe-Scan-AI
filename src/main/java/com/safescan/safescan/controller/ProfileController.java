
package com.safescan.safescan.controller;

import com.safescan.safescan.User;
import com.safescan.safescan.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController {

    private final UserRepository userRepository;

    public ProfileController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/profile")
    public String profile(
            HttpSession session,
            Model model,
            RedirectAttributes redirectAttributes) {

        Object userId = session.getAttribute("userId");

        if (!(userId instanceof Long id)) {
            return "redirect:/login";
        }

        User user = userRepository.findById(id).orElse(null);

        if (user == null) {
            session.invalidate();
            redirectAttributes.addFlashAttribute(
                    "error", "Please log in again.");
            return "redirect:/login";
        }

        model.addAttribute("user", user);
        return "index";
    }
}