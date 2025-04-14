package main.java.com.stockanalysis.controller;

import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/debug")
@RestController
public class DebugAuthController {

    @GetMapping("/session")
    public Map<String, String> debugAuth(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        CsrfToken token = (CsrfToken) request.getAttribute("_csrf");

        return Map.of(
            "sessionId", session != null ? session.getId() : "null",
            "userId", session != null ? String.valueOf(session.getAttribute("USER_ID")) : "null",
            "csrfToken", token != null ? token.getToken() : "null"
        );
}

}




