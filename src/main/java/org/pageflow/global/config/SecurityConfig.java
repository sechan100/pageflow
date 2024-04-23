package org.pageflow.global.config;

import lombok.RequiredArgsConstructor;
import org.pageflow.boundedcontext.auth.adapter.in.web.JwtAuthorizationFilter;
import org.pageflow.boundedcontext.auth.application.springsecurity.oauth2.OAuth2Service;
import org.pageflow.global.filter.ApiExceptionCatchAndDelegatingFilter;
import org.pageflow.global.filter.InFilterForwardedRequestCeaseFilter;
import org.pageflow.global.filter.InternalOnlyUriPretectFilter;
import org.pageflow.global.property.AppProps;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
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
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.session.SessionManagementFilter;

@EnableWebSecurity
@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // props
    private final AppProps appProps;
    // filter
    private final JwtAuthorizationFilter jwtAuthorizationFilter;
    private final ApiExceptionCatchAndDelegatingFilter apiExceptionCatchAndDelegatingFilter;
    private final InFilterForwardedRequestCeaseFilter inFilterForwardedRequestCeaseFilter;
    private final InternalOnlyUriPretectFilter internalOnlyUriPretectFilter;
    // oauth2
    private final OAuth2Service OAuth2Service;
    private final AuthorizationRequestRepository<OAuth2AuthorizationRequest> authorizationRequestRepository;
    // form
    private final AuthenticationProvider authenticationProvider;
    // common
    private final AuthenticationFailureHandler loginFailureHandler;
    private final AuthenticationSuccessHandler loginSuccessHalder;
    private final AuthenticationEntryPoint authEntryPoint;

    @Bean
    SecurityFilterChain pageflowApiChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/user/**").hasRole("USER")
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().permitAll()
            )
            .oauth2Login(oauth2 -> oauth2
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(OAuth2Service)
                )
                .authorizationEndpoint(authorization -> authorization
                    .authorizationRequestRepository(authorizationRequestRepository)
                )
                .successHandler(loginSuccessHalder)
                .failureHandler(loginFailureHandler)
            )
            .formLogin(form -> form
                .loginProcessingUrl("/auth/login")
                .permitAll()
                .successHandler(loginSuccessHalder)
                .failureHandler(loginFailureHandler)
            )
            .authenticationProvider(authenticationProvider)
            .logout(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                /** [세션 생성 정책]
                 * Spring Security는 SecurityContextRepository의 구현을 이용해서 보안 맥락(SecurityContext)를
                 * 저장한다. 기본적으로 HttpSessionSecurityContextRepository를 사용하여 메모리에 박는다.
                 * 아래의 sessionCreationPolicy를 STATELESS로 설정하면, SecurityContextRepository의 구현으로
                 * NullSecurityContextRepository를 사용한다. 해당 구현체는 쓰레드 로컬에서만 접근 가능한 보안 맥락을 생성한다.
                 */
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            /** [SecuirtyFilterChain]
             * 0. DisableEncodeUrlFilter
             * 1. WebAsyncManagerIntegrationFilter
             * 2. SecurityContextHolderFilter
             * 3. HeaderWriterFilter
             * 4. (CUSTOM) ApiExceptionCatchAndDelegatingFilter
             * 5. (CUSTOM) InternalOnlyUriPretectFilter
             * 6. (CUSTOM) JwtAuthorizationFilter
             * 7. OAuth2AuthorizationRequestRedirectFilter
             * 8. OAuth2LoginAuthenticationFilter
             * 9. UsernamePasswordAuthenticationFilter
             * 10. RequestCacheAwareFilter
             * 11. SecurityContextHolderAwareRequestFilter
             * 12. AnonymousAuthenticationFilter
             * 15. SessionManagementFilter
             * 13. (CUSTOM) InFilterForwardedRequestCeaseFilter
             * 14. ExceptionTranslationFilter
             * 15. AuthorizationFilter
             */
            .addFilterAfter(apiExceptionCatchAndDelegatingFilter, HeaderWriterFilter.class)
            .addFilterAfter(internalOnlyUriPretectFilter, ApiExceptionCatchAndDelegatingFilter.class)
            .addFilterAfter(jwtAuthorizationFilter, ApiExceptionCatchAndDelegatingFilter.class)
            .addFilterAfter(inFilterForwardedRequestCeaseFilter, SessionManagementFilter.class)
            .headers(headers -> headers
                .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
            )
            .csrf(AbstractHttpConfigurer::disable)
            .cors(AbstractHttpConfigurer::disable)
            .exceptionHandling(exception -> exception
                .authenticationEntryPoint(authEntryPoint)
//                    .accessDeniedHandler()
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

