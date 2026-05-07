package com.samuel_mc.pickados_api.config;

import com.samuel_mc.pickados_api.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final String allowedOrigins;

    public SecurityConfig(
            CustomUserDetailsService uds,
            JwtAuthenticationFilter jwtAuthenticationFilter,
            @Value("${app.cors.allowed-origins:http://localhost:5173}") String allowedOrigins
    ) {
        this.userDetailsService = uds;
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.allowedOrigins = allowedOrigins;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(HttpSecurity http, PasswordEncoder passwordEncoder)
            throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder);
        return auth.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept", "Origin", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers("/auth/login").permitAll()
                        .requestMatchers("/auth/register-user").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/auth/register-admin").hasAuthority("ROLE_ADMIN")
                        .requestMatchers("/auth/register-tipster").permitAll()
                        .requestMatchers("/auth/availability").permitAll()
                        .requestMatchers("/auth/verify-email").permitAll()
                        .requestMatchers("/auth/request-password-reset").permitAll()
                        .requestMatchers("/auth/reset-password").permitAll()
                        .requestMatchers(HttpMethod.GET, "/analytics/admin/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/roles/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET, "/users/admins").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/users/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET,"/catalogs/**").permitAll()
                        .requestMatchers(HttpMethod.POST,"/catalogs/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.PUT,"/catalogs/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.DELETE,"/catalogs/**").hasAuthority("ROLE_ADMIN")
                        .requestMatchers(HttpMethod.GET,"/sportsbooks").permitAll()
                        .requestMatchers(HttpMethod.GET, "/posts/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/users/public/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/referrals/resolve/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/posts/media/**").hasAuthority("ROLE_TIPSTER")
                        .requestMatchers(HttpMethod.POST, "/posts").hasAuthority("ROLE_TIPSTER")
                        .requestMatchers(HttpMethod.PUT, "/posts/*/pick-status").hasAuthority("ROLE_TIPSTER")
                        .requestMatchers(HttpMethod.GET,"/posts/**").authenticated()
                        .anyRequest().authenticated())
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
