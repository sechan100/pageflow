package org.pageflow.base.security.config;

import lombok.RequiredArgsConstructor;
import org.pageflow.base.constants.CustomProperties;
import org.pageflow.base.security.filter.InsufficientAuthenticationProcessingFilter;
import org.pageflow.base.security.handler.*;
import org.pageflow.domain.user.service.CustomOAuth2UserService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
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
    private final FormLoginAuthenticationSuccessHandler formLoginAuthenticationSuccessHandler;
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final InsufficientAuthenticationProcessingFilter insufficientAuthenticationProcessingFilter;


    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/books/**", "/login", "/signup*", "/verify/email", "/error*", "/common/alertStorage*", "/api/**", "/find").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/files/img/**").permitAll()
                        .anyRequest().authenticated()
                )

                 .formLogin(form -> form
                        .loginPage("/login")
                        .failureHandler(customLoginFailureHandler)
                        .successHandler(formLoginAuthenticationSuccessHandler)
                )

                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )

                .authenticationProvider(daoAuthenticationProvider)

                .csrf(AbstractHttpConfigurer::disable)

                .addFilterAfter(insufficientAuthenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class)

                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                )

                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(customAuthenticationEntryPoint)
                );


        return http.build();
    }


}
