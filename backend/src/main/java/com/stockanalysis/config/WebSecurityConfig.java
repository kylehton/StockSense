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
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList(
            "Content-Type", 
            "Authorization", 
            "X-XSRF-TOKEN",
            "Access-Control-Allow-Origin",
            "Access-Control-Allow-Credentials",
            "Access-Control-Allow-Headers",
            "JSESSIONID"
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
                // Enable CSRF protection with a custom token repository, readable by JavaScript
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())

                // Custom CSRF matcher to exclude certain paths from CSRF protection
                .requireCsrfProtectionMatcher(request -> 
                    !"OPTIONS".equals(request.getMethod()) && 
                    !"/xsrf".equals(request.getServletPath()) &&
                    !"/google/auth".equals(request.getServletPath()) &&
                    !"/check".equals(request.getServletPath()) &&
                    CsrfFilter.DEFAULT_CSRF_MATCHER.matches(request)
                )
            )
            
            // Session management (prevent expired sessions prohibiting functionality)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                .maximumSessions(1)
                // '/xsrf' endpoint called upon expiring of session to re-instantiate a new session
                .expiredUrl("/xsrf")
            )
            
            // Request authorization
            .authorizeHttpRequests(auth -> {
                logger.debug("Configuring authorization rules...");
                auth
                    // CORS preflight requests
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                    
                    // Public endpoints
                    .requestMatchers("/xsrf", "/google/auth", "/check").permitAll()
                    
                    // Semi-public endpoints that need session but not full auth
                    .requestMatchers("/getsession", "db/getsymbols").permitAll()
                    
                    // Protected endpoints that need full authentication
                    .requestMatchers("/db/addsymbol", "/db/deletesymbol", "/db/adduser", "/news/generate", "/news/get").hasAuthority("USER")
                    
                    // Default to requiring authentication
                    .anyRequest().authenticated();
            })
            
            // Authentication filter
            .addFilterBefore(new OncePerRequestFilter() {
                @Override
                protected void doFilterInternal(HttpServletRequest request, 
                        HttpServletResponse response, FilterChain chain)
                        throws ServletException, IOException {
                    
                    HttpSession session = request.getSession(false);
                    
                    if (session != null) {
                        Object userId = session.getAttribute("USER_ID");
                        if (userId != null) {
                            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
                            
                            if (currentAuth == null || !currentAuth.isAuthenticated() || 
                                currentAuth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
                                
                                List<GrantedAuthority> authorities = Arrays.asList(
                                    new SimpleGrantedAuthority("USER")
                                );
                                
                                Authentication auth = new UsernamePasswordAuthenticationToken(
                                    userId.toString(), 
                                    null,
                                    authorities
                                );
                                
                                SecurityContextHolder.getContext().setAuthentication(auth);
                            }
                        }
                    }
                    
                    chain.doFilter(request, response);
                    
                }
            }, UsernamePasswordAuthenticationFilter.class)
            
            // Exception handling
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    logger.error("Auth error for {} (Session: {}): {}", 
                        request.getRequestURI(),
                        request.getSession(false) != null ? request.getSession().getId() : "null",
                        authException.getMessage());
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().write("Authentication required");
                })
            );

        logger.info("Security filter chain configured successfully");

        return http.build();
}
}
