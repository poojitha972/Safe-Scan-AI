
package com.safescan.safescan.controller;

import com.safescan.safescan.User;
import com.safescan.safescan.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository,
                          PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @GetMapping("/admin/login")
    public String adminLoginPage() {
        return "admin-login";
    }

    @GetMapping("/signup")
    public String signupPage() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttributes) {

        String cleanName = name.trim();
        String cleanEmail = email.trim().toLowerCase();

        if (cleanName.isEmpty() || password.length() < 8) {
            redirectAttributes.addFlashAttribute(
                    "error",
                    "Enter a name and a password of at least 8 characters.");
            return "redirect:/signup";
        }

        if (userRepository.existsByEmail(cleanEmail)) {
            redirectAttributes.addFlashAttribute(
                    "error", "This email is already registered.");
            return "redirect:/signup";
        }

        User user = new User(
                cleanName,
                cleanEmail,
                passwordEncoder.encode(password));

        userRepository.save(user);

        redirectAttributes.addFlashAttribute(
                "success", "Account created! Please log in.");
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String login(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        return userRepository.findByEmail(email.trim().toLowerCase())
                .filter(user -> passwordEncoder.matches(
                        password, user.getPassword()))
                .map(user -> {
                    session.setAttribute("userId", user.getId());
                    return "redirect:/profile";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute(
                            "error", "Invalid email or password.");
                    return "redirect:/login";
                });
    }

    @PostMapping("/admin/login")
    public String adminLogin(
            @RequestParam String email,
            @RequestParam String password,
            HttpSession session,
            RedirectAttributes redirectAttributes) {

        return userRepository.findByEmail(email.trim().toLowerCase())
                .filter(user -> "ADMIN".equals(user.getRole()))
                .filter(user -> passwordEncoder.matches(
                        password, user.getPassword()))
                .map(user -> {
                    session.removeAttribute("userId");
                    session.setAttribute("adminUserId", user.getId());
                    return "redirect:/admin";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute(
                            "error", "Invalid admin email or password.");
                    return "redirect:/admin/login";
                });
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
