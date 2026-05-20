package com.capstone.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

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

    public SecurityConfig(
            JwtConverter jwtConverter,
            OidcUserServiceImpl oidcUserService,
            OAuth2LoginSuccessHandler loginSuccessHandler
    ) {
        this.jwtConverter = jwtConverter;
        this.oidcUserService = oidcUserService;
        this.loginSuccessHandler = loginSuccessHandler;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .csrf(csrf -> csrf.disable())

            .authorizeHttpRequests(auth -> auth

                // Public pages
                .requestMatchers(
                        "/",
                        "/Home.html",
                        "/login.html",
                        "/reward.html",
                        "/report.html",
                        "/sponsor-dashboard.html",
                        "/css/**",
                        "/js/**",
                        "/images/**"
                )
                .permitAll()

                // Role-protected pages
                .requestMatchers("/sponsor-dashboard.html").hasRole(SPONSOR)
                .requestMatchers("/user-dashboard.html").hasRole(USER)
                .requestMatchers("/municipal-dashboard.html").hasRole(MUNICIPAL)
                .requestMatchers("/admin-dashboard.html").hasRole(ADMIN)

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
                .requestMatchers("/api/sponsors/me").hasRole(SPONSOR)
                .requestMatchers("/api/sponsors/**").hasRole(ADMIN)

                // Other role APIs
                .requestMatchers("/api/admin/**").hasRole(ADMIN)
                .requestMatchers("/api/user/**").hasRole(USER)
                .requestMatchers("/api/municipal/**").hasRole(MUNICIPAL)
                .requestMatchers("/api/sponsor/**").hasRole(SPONSOR)

                .requestMatchers("/api/sync").authenticated()
                .anyRequest().authenticated()
            )

            // OIDC login
            .oauth2Login(oauth2 -> oauth2
                .successHandler(loginSuccessHandler)
                .userInfoEndpoint(userInfo ->
                    userInfo.oidcUserService(oidcUserService)
                )
            )

            // JWT resource server (for API token auth)
            .oauth2ResourceServer(oauth2 ->
                oauth2.jwt(jwt ->
                    jwt.jwtAuthenticationConverter(jwtConverter)
                )
            )

            // Logout configuration
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/Home.html")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            );

        return http.build();
    }
}