package br.hallel.relational.api.app.security;

import br.hallel.relational.api.app.security.exceptions.handler.CustomAccessDeniedHandler;
import br.hallel.relational.api.app.security.ministry.MinistryCoordinatorFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;

    private final JwtTokenFilter jwtTokenFilter;
    private final MinistryCoordinatorFilter ministryCoordinatorFilter;


    @Bean
    @Order(1)
    public SecurityFilterChain ministryFilterChain(HttpSecurity http) throws Exception {
        http
                // Este filtro SÓ se aplica a rotas que começam com /coordinator/
                .securityMatcher("/coordinator/**")
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(authorize -> authorize
                        .anyRequest()
                        .hasAnyAuthority("COORDINATOR", "VICE_COORDINATOR", "EXTERNAL_COORDINATOR", "ADMIN")
                )
                .exceptionHandling(configurer -> configurer.accessDeniedHandler(accessDeniedHandler()))
                .addFilterBefore(ministryCoordinatorFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((authorizeRequests) -> {
                            authorizeRequests
                                    .requestMatchers("/swagger.html", "/swagger-ui/**", "/swagger-ui.html", "/docs/**",
                                            "/v3/api-docs/**").permitAll()
                                    .requestMatchers("/auth/**").permitAll()
                                    .requestMatchers("/error").permitAll()
                                    .requestMatchers("/public/**").permitAll()

                                    .requestMatchers("/admin/event/**").hasRole("ADMIN")
                                    .requestMatchers("/admin/ministry/**").hasRole("ADMIN")
                                    .requestMatchers("/admin/user/**").hasRole("ADMIN")
                                    .requestMatchers("/user/**").hasRole("USER")
                                    .requestMatchers("/ws-scale-chat").hasRole("USER");
                        }
                )
                .exceptionHandling(configurer -> configurer
                        .accessDeniedHandler(accessDeniedHandler()))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOriginPattern("*");
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

}
