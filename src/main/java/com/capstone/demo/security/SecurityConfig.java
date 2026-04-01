package com.capstone.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    public static final String ADMIN = "Admin";
    public static final String USER = "User";
    public static final String SPONSOR = "Sponsor";
    public static final String MUNICIPAL = "MunicipalOfficer";

    @Autowired
    private JwtConverter jwtConverter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/",
                        "/Home.html",
                        "/login.html",
                        "/reward.html",
                        "report.html",
                        "/css/**",
                        "/js/**",
                        "/images/**"
                ).permitAll()
                .requestMatchers("/api/admin/**").hasRole(ADMIN)
                .requestMatchers("/api/user/**").hasRole(USER)
                .requestMatchers("/api/municipal/**").hasRole(MUNICIPAL)
                .requestMatchers("/api/sponsor/**").hasRole(SPONSOR)
                .anyRequest().authenticated()
            )

            
            .oauth2Login(Customizer.withDefaults())

            
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(jwtConverter)
                )
            );

        return http.build();
    }
}