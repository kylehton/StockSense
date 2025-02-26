package com.stockanalysis.config;

import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class SessionTracker implements HttpSessionListener {
    private static final Set<String> activeSessions = Collections.synchronizedSet(new HashSet<>());

    @Override
    public void sessionCreated(HttpSessionEvent event) {
        activeSessions.add(event.getSession().getId());
        System.out.println("Session Created: " + event.getSession().getId());
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent event) {
        activeSessions.remove(event.getSession().getId());
        System.out.println("Session Destroyed: " + event.getSession().getId());
    }

    public static Set<String> getActiveSessions() {
        return activeSessions;
    }

    public static void removeSession(String sessionId) {
        activeSessions.remove(sessionId);
    }
}
