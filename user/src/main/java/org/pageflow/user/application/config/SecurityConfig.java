package org.pageflow.user.application.config;

import lombok.RequiredArgsConstructor;
import org.pageflow.common.property.ApplicationProperties;
import org.pageflow.user.adapter.in.auth.form.LoginUri;
import org.pageflow.user.adapter.in.auth.oauth2.OAuth2Service;
import org.pageflow.user.adapter.in.filter.DevOnlyJwtSessionFixFilter;
import org.pageflow.user.adapter.in.filter.ExceptionCatchFilter;
import org.pageflow.user.adapter.in.filter.JwtAuthorizationFilter;
import org.pageflow.user.adapter.in.filter.PrivateUriPretectFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.expression.SecurityExpressionHandler;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.client.web.AuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.expression.DefaultWebSecurityExpressionHandler;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.header.HeaderWriterFilter;
import org.springframework.security.web.servlet.util.matcher.MvcRequestMatcher;
import org.springframework.web.servlet.handler.HandlerMappingIntrospector;


@EnableWebSecurity
@Configuration
//@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

  // props
  private final ApplicationProperties appProps;
  // filter
  private final JwtAuthorizationFilter jwtAuthorizationFilter;
  private final ExceptionCatchFilter exceptionCatchFilter;
  private final PrivateUriPretectFilter privateUriPretectFilter;
  private final DevOnlyJwtSessionFixFilter devOnlyJwtSessionFixFilter;
  // role hierarchy
  private final RoleHierarchy roleHierarchy;
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
  SecurityFilterChain pageflowApiChain(HttpSecurity http, HandlerMappingIntrospector introspector) throws Exception {
    MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
    http
      .authorizeHttpRequests(auth -> auth
        .requestMatchers(mvcMatcherBuilder.pattern("/user/**")).hasRole("USER")
        .requestMatchers(mvcMatcherBuilder.pattern("/admin/**")).hasRole("ADMIN")
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
        .loginProcessingUrl(LoginUri.SPRING_SECURITY_FORM_LOGIN_URI)
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
       * 4. (CUSTOM) ExceptionCatchFilter
       * 5. (CUSTOM) PrivateUriPretectFilter
       * 6. (DEV_ONLY) DevOnlyJwtSessionFixFilter
       * 6. (CUSTOM) JwtAuthorizationFilter
       * 7. OAuth2AuthorizationRequestRedirectFilter
       * 8. OAuth2LoginAuthenticationFilter
       * 9. UsernamePasswordAuthenticationFilter
       * 10. RequestCacheAwareFilter
       * 11. SecurityContextHolderAwareRequestFilter
       * 12. AnonymousAuthenticationFilter
       * 13. SessionManagementFilter
       * 14. ExceptionTranslationFilter
       * 15. AuthorizationFilter
       */
      .addFilterAfter(exceptionCatchFilter, HeaderWriterFilter.class)
      .addFilterAfter(privateUriPretectFilter, ExceptionCatchFilter.class)
      .addFilterAfter(devOnlyJwtSessionFixFilter, PrivateUriPretectFilter.class)
      .addFilterAfter(jwtAuthorizationFilter, DevOnlyJwtSessionFixFilter.class)
      .headers(headers -> headers
        .frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
      )
      // TODO: 정책 검토하기(csrf, cors)
      .csrf(AbstractHttpConfigurer::disable)
      .cors(AbstractHttpConfigurer::disable)
      .exceptionHandling(exception -> exception
        .authenticationEntryPoint(authEntryPoint)
//        .accessDeniedHandler()
      );
    return http.build();
  }

  @Bean
  public WebSecurityCustomizer webSecurityCustomizer(HandlerMappingIntrospector introspector) {
    MvcRequestMatcher.Builder mvcMatcherBuilder = new MvcRequestMatcher.Builder(introspector);
    return web -> web.ignoring()
      .requestMatchers(mvcMatcherBuilder.pattern("/h2-console/**"))
      .requestMatchers(mvcMatcherBuilder.pattern("/favicon.ico"))
      .requestMatchers(mvcMatcherBuilder.pattern("/files/**"))
//      .requestMatchers(mvcMatcherBuilder.pattern("/error/**"))
      .requestMatchers(mvcMatcherBuilder.pattern("/css/**"))
      .requestMatchers(mvcMatcherBuilder.pattern("/js/**"))
      .requestMatchers(mvcMatcherBuilder.pattern("/swagger-ui/**"))
      .requestMatchers(mvcMatcherBuilder.pattern("/swagger-resources/**"))
      .requestMatchers(mvcMatcherBuilder.pattern("/v3/api-docs/**"))
    ;
  }


  private SecurityExpressionHandler<FilterInvocation> _webExpressionHandler() {
    DefaultWebSecurityExpressionHandler defaultWebSecurityExpressionHandler = new DefaultWebSecurityExpressionHandler();
    defaultWebSecurityExpressionHandler.setRoleHierarchy(roleHierarchy);
    return defaultWebSecurityExpressionHandler;
  }

}

