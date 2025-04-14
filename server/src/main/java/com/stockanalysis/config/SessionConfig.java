package com.stockanalysis.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@Configuration
@EnableRedisHttpSession // enables Redis-backed session storage
public class SessionConfig {
    // @EnableRedisHttpSession does everything needed for session management
}
