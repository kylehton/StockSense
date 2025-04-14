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
                .ignoringRequestMatchers("/xsrf", "/google/auth", "/db/check", "/debug/auth", "/getsession", "/db/getsymbols")
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED) // Enforce stateless session
            )
            .authorizeHttpRequests(auth -> {
                logger.debug("Configuring authorization rules...");
                auth
                    .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll() // Allow CORS preflight requests
                    .requestMatchers("/xsrf", "/google/auth", "/db/check", "/debug/auth").permitAll()
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
                        if (userId != null) {
                            Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
                            if (currentAuth == null || !currentAuth.isAuthenticated()) {
                                List<GrantedAuthority> authorities = Arrays.asList(new SimpleGrantedAuthority("USER"));
                                Authentication auth = new UsernamePasswordAuthenticationToken(userId.toString(), null, authorities);
                                SecurityContextHolder.getContext().setAuthentication(auth);
                            }
                        }
                    } else {
                        SecurityContextHolder.clearContext(); // Allow anonymous access if no session
                    }

                    chain.doFilter(request, response);
                }
            }, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    String requestPath = request.getServletPath();
                    if (requestPath.matches("/xsrf|/google/auth|/db/check|/debug/auth|/getsession|/db/getsymbols")) {
                        response.setStatus(HttpStatus.OK.value());
                        return;
                    }
                    logger.error("Auth error for {}: {}", request.getRequestURI(), authException.getMessage());
                    response.setStatus(HttpStatus.UNAUTHORIZED.value());
                    response.getWriter().write("Authentication required");
                })
            );

        logger.info("Security filter chain configured successfully");
        return http.build();
    }
}
