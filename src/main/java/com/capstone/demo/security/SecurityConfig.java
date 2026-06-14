package com.capstone.demo.security;

import com.capstone.demo.security.RateLimitFilter;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    public static final String ADMIN = "Admin";
    public static final String USER = "User";
    public static final String SPONSOR = "Sponsor";
    public static final String MUNICIPAL = "MunicipalOfficer";

    private final JwtConverter jwtConverter;
    private final OidcUserServiceImpl oidcUserService;
    private final OAuth2LoginSuccessHandler loginSuccessHandler;
    private final RateLimitFilter rateLimitingFilter;

    public SecurityConfig(
            JwtConverter jwtConverter,
            OidcUserServiceImpl oidcUserService,
            OAuth2LoginSuccessHandler loginSuccessHandler,
            RateLimitFilter rateLimitingFilter
    ) {
        this.jwtConverter = jwtConverter;
        this.oidcUserService = oidcUserService;
        this.loginSuccessHandler = loginSuccessHandler;
        this.rateLimitingFilter = rateLimitingFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())

            // Rate limiting
            .addFilterBefore(rateLimitingFilter, UsernamePasswordAuthenticationFilter.class)

            .authorizeHttpRequests(auth -> auth

                // Public pages
                .requestMatchers(
                        "/",
                        "/Home.html",
                        "/login.html",
                        "/reward.html",
                        "/report.html",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/favicon.ico"
                ).permitAll()

                // Role-protected pages
                .requestMatchers("/sponsor-dashboard.html").hasRole(SPONSOR)
                .requestMatchers("/user-dashboard.html").hasRole(USER)
                .requestMatchers("/municipal-dashboard.html").hasRole(MUNICIPAL)
                .requestMatchers("/admin-dashboard.html").hasRole(ADMIN)

                // Issue APIs
                .requestMatchers("/report.html").hasRole(USER)
                .requestMatchers("/api/issues/submit").hasRole(USER)
                .requestMatchers("/api/issues/my").hasRole(USER)
                .requestMatchers("/api/issues/officer/**").hasRole(MUNICIPAL)
                .requestMatchers("/api/issues/resolve/**").hasRole(MUNICIPAL)
                .requestMatchers("/api/issues/assign/**").hasRole(ADMIN)
                .requestMatchers("/api/issues/**").authenticated()

                // Reward APIs
                .requestMatchers("/api/rewards/sponsor").hasRole(SPONSOR)
                .requestMatchers("/api/rewards/admin").hasRole(ADMIN)
                .requestMatchers("/api/rewards/user").hasRole(USER)
                .requestMatchers("/api/rewards/**").authenticated()

                // Reward transaction APIs
                .requestMatchers("/api/reward-transactions/sponsor/**").hasRole(SPONSOR)
                .requestMatchers("/api/reward-transactions/user/**").hasRole(USER)
                .requestMatchers("/api/reward-transactions/admin/**").hasRole(ADMIN)

                // Sponsor profile APIs
                .requestMatchers("/api/sponsor/me").hasRole(SPONSOR)
                .requestMatchers("/api/sponsor/**").hasRole(SPONSOR)
                .requestMatchers("/api/sponsors/**").hasRole(ADMIN)

                // Other role APIs
                .requestMatchers("/api/admin/**").hasRole(ADMIN)
                .requestMatchers("/api/user/**").hasRole(USER)
                .requestMatchers("/api/municipal/**").hasRole(MUNICIPAL)

                
                .requestMatchers("/api/debug/**").authenticated()
                .requestMatchers("/api/sync").authenticated()

                .anyRequest().authenticated()
            )

            .oauth2Login(oauth2 -> oauth2
                .successHandler(loginSuccessHandler)
                .userInfoEndpoint(userInfo ->
                    userInfo.oidcUserService(oidcUserService)
                )
            )

            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(jwtConverter)
                )
            )

            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/Home.html")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOrigins(List.of(
                "http://localhost:8090",
                "http://localhost:5500",
                "http://127.0.0.1:5500"
        ));

        config.setAllowedMethods(List.of(
                "GET", "POST", "PUT", "DELETE", "OPTIONS"
        ));

        config.setAllowedHeaders(List.of(
                "Authorization",
                "Content-Type"
        ));

        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }
}