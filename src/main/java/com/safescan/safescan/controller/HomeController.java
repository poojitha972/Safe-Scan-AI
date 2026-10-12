
package com.safescan.safescan.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session) {
        Object userId = session.getAttribute("userId");

        if (userId instanceof Long) {
            return "redirect:/profile";
        }

        return "redirect:/login";
    }
}