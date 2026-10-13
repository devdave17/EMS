
package com.IIITV.EMS.Security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)
            throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> headers
                        .contentTypeOptions(options -> {})
                        .httpStrictTransportSecurity(hsts -> hsts
                                .includeSubDomains(true)
                                .maxAgeInSeconds(31536000)
                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll()
                );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // Same-origin HTML/CSS/JS needs no CORS.
        // These settings support cross-origin requests if needed.
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "PATCH",
                        "DELETE", "OPTIONS", "HEAD")
        );
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of(
                "ETag", "Location", "Retry-After",
                "X-RateLimit-Limit", "X-RateLimit-Remaining"
        ));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", config);

        return source;
    }

    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilterRegistration(
            RateLimitFilter rateLimitFilter) {

        FilterRegistrationBean<RateLimitFilter> registration =
                new FilterRegistrationBean<>();

        registration.setFilter(rateLimitFilter);
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);

        return registration;
    }


    @Bean
    public FilterRegistrationBean<BearerTokenFilter> bearerTokenFilterRegistration(
            BearerTokenFilter bearerTokenFilter) {

        FilterRegistrationBean<BearerTokenFilter> registration =
                new FilterRegistrationBean<>();

        registration.setFilter(bearerTokenFilter);
        registration.addUrlPatterns("/api/*");
        registration.setOrder(2);

        return registration;
    }

}
