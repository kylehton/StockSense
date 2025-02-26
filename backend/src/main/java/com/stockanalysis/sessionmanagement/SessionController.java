package com.stockanalysis.sessionmanagement;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
public class SessionController {

    @GetMapping("/list-sessions")
    public String listSessions() {
        Set<String> sessions = SessionTracker.getActiveSessions();
        if (sessions.isEmpty()) {
            return "No active sessions.";
        }
        return "Active Sessions: " + String.join(", ", sessions);
    }
}
