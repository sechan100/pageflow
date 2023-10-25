package org.pageflow.base.security.config;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.constants.CustomProperties;
import org.pageflow.base.security.filter.InsufficientAuthenticationProcessingFilter;
import org.pageflow.base.security.handler.CustomAuthenticationEntryPoint;
import org.pageflow.base.security.handler.CustomLoginFailureHandler;
import org.pageflow.domain.user.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
    private final AuthenticationProvider daoAuthenticationProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomProperties customProperties;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    private final CustomLoginFailureHandler customLoginFailureHandler;
    private final InsufficientAuthenticationProcessingFilter insufficientAuthenticationProcessingFilter;
    
    
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/login", "/signup*", "/verify/email", "/error*", "/common/alertStorage*").permitAll()
                        .anyRequest().authenticated()
                )
                
                .formLogin(form -> form
                        .loginPage("/login")
                        .failureHandler(customLoginFailureHandler)
                )
                
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                )
                
                .authenticationProvider(daoAuthenticationProvider)
                
                .csrf(AbstractHttpConfigurer::disable)
                
                .addFilterAfter(insufficientAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class)
                
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/").permitAll()
                )
        
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                );
        
        
        return http.build();
    }
    
    // static resource allow
    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/css/**", "/js/**", "/img/**");
    }
 
    
}
