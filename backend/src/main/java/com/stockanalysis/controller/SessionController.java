package com.stockanalysis.controller;

import com.stockanalysis.config.SessionTracker;
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

    @GetMapping("/invalidate-session")
    public String invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false); // Get existing session, do NOT create a new one
        if (session != null) {
            String sessionId = session.getId();
            if (SessionTracker.getActiveSessions().contains(sessionId)) {
                System.out.println("Invalidating session: " + sessionId);
                session.invalidate();
                SessionTracker.removeSession(sessionId); // Ensure it's removed from activeSessions
                return "Session " + sessionId + " invalidated!";
            }
        }
        return "No matching active session found!";
    }
}
