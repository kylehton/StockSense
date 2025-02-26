package com.stockanalysis.sessionmanagement;

import jakarta.servlet.http.HttpSessionListener;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Component
public class SessionTracker implements HttpSessionListener {
    private static final Set<String> activeSessions = Collections.synchronizedSet(new HashSet<>());

    public static Set<String> getActiveSessions() {
        return activeSessions;
    }

}
