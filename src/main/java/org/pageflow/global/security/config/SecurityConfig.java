package org.pageflow.global.security.config;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.user.service.CustomOAuth2UserService;
import org.pageflow.global.constants.CustomProps;
import org.pageflow.global.security.filter.JwtAuthorizationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    
//    private final AuthenticationProvider daoAuthenticationProvider;
    private final CustomOAuth2UserService customOAuth2UserService;
    private final CustomProps customProps;
    private final AuthenticationEntryPoint pageflowAuthenticationEntryPoint;
    private final AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;
    
    @Bean
    SecurityFilterChain pageflowApiChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/user/**").hasRole("USER")
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .requestMatchers("/signup/**", "/login/**", "/logout/**", "/internal/**", "/refresh/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth2 -> oauth2
                        .failureHandler(oAuth2AuthenticationFailureHandler)
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService)
                        )
                        .authorizationEndpoint(authorization -> authorization
                                .authorizationRequestRepository(authorizationRequestRepository)
                        )
                        .successHandler(oAuth2AuthenticationSuccessHandler)
                )
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session
                        /* [세션 생성 정책]
                         * Spring Security는 SecurityContextRepository의 구현을 이용해서 보안 맥락(SecurityContext)를
                         * 저장한다. 기본적으로 HttpSessionSecurityContextRepository를 사용하여 메모리에 박는다.
                         * 아래의 sessionCreationPolicy를 STATELESS로 설정하면, SecurityContextRepository의 구현으로
                         * NullSecurityContextRepository를 사용한다. 해당 구현체는 쓰레드 로컬에서만 접근 가능한 보안 맥락을 생성한다.
                         * */
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class)
                .headers(headers -> headers
                        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint(pageflowAuthenticationEntryPoint)
//                        .accessDeniedHandler()
                );
        return http.build();
    }
    
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring().requestMatchers(
                "/h2-console/**",
                "/favicon.ico",
                "/error/**",
                "/css/**",
                "/js/**",
                "/img/**",
                "/files/img/**",
                "/swagger-ui/**",
                "/swagger-resources/**",
                "/v3/api-docs/**"
        );
    }
    
}

