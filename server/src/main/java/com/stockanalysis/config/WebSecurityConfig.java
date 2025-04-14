package com.stockanalysis.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.OncePerRequestFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", "https://stock-sense-client.vercel.app/"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
            "Content-Type", "Authorization", "X-XSRF-TOKEN",
            "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials",
            "Access-Control-Allow-Headers", "JSESSIONID"
        ));
        configuration.setAllowCredentials(true);
        configuration.setExposedHeaders(Arrays.asList("Set-Cookie", "X-XSRF-TOKEN"));

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.info("Configuring security filter chain...");

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                .ignoringRequestMatchers("/xsrf", "/google/auth", "/db/check", "/debug/session", "/db/testdb", "/getsession", "/db/getsymbols")
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Enforce stateless session
            )
            .authorizeHttpRequests(auth -> {
                logger.debug("Configuring authorization rules...");
                auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow CORS preflight requests
                    .requestMatchers("/xsrf", "/google/auth", "/db/check", "/debug/auth", "/error").permitAll()
                    .requestMatchers("/getsession", "/db/getsymbols").permitAll()
                    .requestMatchers("/db/addsymbol", "/db/deletesymbol", "/db/adduser", "/news/generate", "/news/get",
                                     "/s3/retrieve", "/db/setnewskey", "/db/getnewskey").hasAuthority("USER")
                    .anyRequest().authenticated();
            })
            .addFilterBefore(new OncePerRequestFilter() {
                @Override
                protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                                FilterChain chain) throws ServletException, IOException {
                    HttpSession session = request.getSession(false);
            
                    if (session != null) {
                        Object userId = session.getAttribute("USER_ID");
                        String csrfToken = (String) session.getAttribute("org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN");
            
                        logger.debug("Session ID: {}", session.getId());
                        logger.debug("USER_ID from session: {}", userId);
                        logger.debug("CSRF token in session: {}", csrfToken);
            
                        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
                        if (userId != null && (currentAuth == null || !currentAuth.isAuthenticated())) {
                            List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("USER"));
                            Authentication auth = new UsernamePasswordAuthenticationToken(userId.toString(), null, authorities);
                            SecurityContextHolder.getContext().setAuthentication(auth);
                        }
                    } else {
                        logger.debug("No session found. Clearing security context.");
                        SecurityContextHolder.clearContext(); // Allow anonymous access if no session
                    }
            
                    chain.doFilter(request, response);
                }
            }, UsernamePasswordAuthenticationFilter.class)
            
            .exceptionHandling(ex -> ex
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    logger.error("Access Denied on {} {} - {}", request.getMethod(), request.getRequestURI(), accessDeniedException.getMessage());
                
                    try {
                        HttpSession session = request.getSession(false);
                        if (session != null) {
                            Object userId = session.getAttribute("USER_ID");
                            Object csrfToken = session.getAttribute("org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN");
                
                            logger.info("Session ID: {}", session.getId());
                            logger.info("USER_ID: {}", userId != null ? userId.toString() : "null");
                            logger.info("CSRF Token (session): {}", csrfToken != null ? csrfToken.toString() : "null");
                        } else {
                            logger.info("No session present during access denied.");
                        }
                
                        String csrfHeader = request.getHeader("X-XSRF-TOKEN");
                        logger.info("CSRF Token (header): {}", csrfHeader != null ? csrfHeader : "null");
                
                        response.setStatus(HttpStatus.FORBIDDEN.value());
                        response.getWriter().write("CSRF verification failed or access denied.");
                    } catch (Exception e) {
                        logger.error("Failed to log CSRF debug info", e);
                    }
                })
            );

        logger.info("Security filter chain configured successfully");
        return http.build();
    }
}
