package br.hallel.relational.api.app.security;

import br.hallel.relational.api.app.security.exceptions.handler.CustomAccessDeniedHandler;
import br.hallel.relational.api.app.security.ministry.MinistryCoordinatorFilter;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final AuthenticationProvider authenticationProvider;

    private final JwtTokenFilter jwtTokenFilter;
    private final MinistryCoordinatorFilter ministryCoordinatorFilter;


    public Filter debugFilter() {
        return new Filter() {
            @Override
            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
                    throws IOException, ServletException {

                HttpServletRequest req = (HttpServletRequest) request;
                HttpServletResponse res = (HttpServletResponse) response;

                System.out.println("🔍 DEBUG FILTER: " + req.getMethod() + " " + req.getRequestURI());
                System.out.println("🔍 Origin: " + req.getHeader("Origin"));

                chain.doFilter(request, response);

                String corsOrigin = res.getHeader("Access-Control-Allow-Origin");
                System.out.println("🔍 Response CORS: " + corsOrigin);

                if ("*".equals(corsOrigin)) {
                    System.out.println("❌ CORS COM '*' DETECTADO!");
                }
                System.out.println("----------------------------------------");
            }
        };
    }


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
                //.addFilterBefore(debugFilter(), UsernamePasswordAuthenticationFilter.class)
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
                                    .requestMatchers("/payment/pix/**").permitAll()
                                    .requestMatchers("/admin/event/**").hasRole("ADMIN")
                                    .requestMatchers("/admin/ministry/**").hasRole("ADMIN")
                                    .requestMatchers("/admin/user/**").hasRole("ADMIN")
                                    .requestMatchers("/user/**").hasRole("USER")
                                    .requestMatchers("/ws-scale-chat").hasRole("USER")
                                    .requestMatchers("/ws-auth/**").permitAll()
                                    .requestMatchers("/ws-payment/**").permitAll();
                        }
                )
                .exceptionHandling(configurer -> configurer
                        .accessDeniedHandler(accessDeniedHandler()))
                .authenticationProvider(authenticationProvider)
                //.addFilterBefore(debugFilter(), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(corsFilter(), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        if (getNgrokUrl() == null) {
            config.setAllowCredentials(true);
        }
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedOrigin("http://localhost:3000");
        config.addAllowedOrigin("https://comunidadecatolicahallel.com.br");
        config.addAllowedOrigin("*");

        String ngrokUrl = getNgrokUrl();
        if (ngrokUrl != null) {
            config.addAllowedOrigin(ngrokUrl);
            System.out.println("🌐 CORS configurado para: " + ngrokUrl);
        }
        config.addAllowedHeader("*");
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }

    private String getNgrokUrl() {
        try {
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.setRequestFactory(new SimpleClientHttpRequestFactory());

            String response = restTemplate.getForObject("http://localhost:4040/api/tunnels", String.class);

            if (response != null) {
                // Parse simples para pegar a URL HTTPS
                String[] parts = response.split("\"public_url\":\"https://");
                if (parts.length > 1) {
                    String url = "https://" + parts[1].split("\"")[0];
                    return url;
                }
            }
        } catch (Exception e) {
            System.out.println("⚠️  Ngrok não detectado na porta 4040");
        }
        return null;
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return new CustomAccessDeniedHandler();
    }

}
