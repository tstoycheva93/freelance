package com.thesis.freelance.config;

import com.thesis.freelance.security.MaintenanceModeFilter;
import com.thesis.freelance.security.jwt.JwtAuthenticationEntryPoint;
import com.thesis.freelance.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final MaintenanceModeFilter maintenanceModeFilter;

    /*@Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers("/ws/**");
    }*/

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp
                                .policyDirectives("default-src 'self'; connect-src 'self' ws://localhost:8080 wss://localhost:8080 http://localhost:5173")
                        )
                )
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        //publichni
                        .requestMatchers("/ws/**").permitAll()
                        .requestMatchers("/auth/**").permitAll()
                        .requestMatchers("/home/**").permitAll()
                        .requestMatchers("/roles").permitAll()
                        .requestMatchers("/error").permitAll()
                        .requestMatchers("/system/public/**").permitAll()

                        //za admin
                        .requestMatchers("/office/**").hasRole("ADMIN")

                        //za useri
                        .requestMatchers("/user/**").authenticated()
                        .requestMatchers("/locations/**").authenticated()
                        .requestMatchers("/profile/**").authenticated()
                        .requestMatchers("/postings/**").authenticated()
                        .requestMatchers("/skills/**").authenticated()
                        .requestMatchers("/client/**").authenticated()
                        .requestMatchers("/freelancer/**").authenticated()
                        .requestMatchers("/education/**").authenticated()
                        .requestMatchers("/work/**").authenticated()
                        .requestMatchers("/certifications/**").authenticated()
                        .requestMatchers("/cv/**").authenticated()
                        .requestMatchers("/conversations/**").authenticated()
                        .requestMatchers("/notifications/**").authenticated()
                        .requestMatchers("/flags/**").authenticated()

                        .requestMatchers("/swagger-ui/**").permitAll()
                        .requestMatchers("/v3/api-docs/**").permitAll()
                        .requestMatchers("/swagger-ui/index.html").permitAll()
                        .requestMatchers("/cert/scheduler/**").permitAll()



                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterAfter(maintenanceModeFilter, JwtAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Alternative: Create AuthenticationManager manually to ensure it uses our provider.
     * This ensures the UserDetailsService (with activity status check) is properly integrated.
     */
    /*
    @Bean
    public AuthenticationManager authenticationManager() {
        ProviderManager providerManager = new ProviderManager(authenticationProvider());
        return providerManager;
    }
    */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("http://localhost:5173"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        configuration.addAllowedHeader("Sec-WebSocket-Version");
        configuration.addAllowedHeader("Sec-WebSocket-Key");
        configuration.addAllowedHeader("Sec-WebSocket-Extensions");
        configuration.addAllowedHeader("Sec-WebSocket-Protocol");

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}