package org.pageflow.user.application.config;

import jakarta.servlet.Filter;
import org.pageflow.user.adapter.in.auth.form.AuthenticationTokenPrivder;
import org.pageflow.user.adapter.in.filter.DevOnlyJwtSessionFixFilter;
import org.pageflow.user.adapter.in.filter.ExceptionCatchFilter;
import org.pageflow.user.adapter.in.filter.JwtAuthorizationFilter;
import org.pageflow.user.adapter.in.filter.PrivateUriPretectFilter;
import org.pageflow.user.port.in.SessionUseCase;
import org.pageflow.user.port.in.TokenUseCase;
import org.pageflow.user.port.out.LoadAccountPort;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.HandlerExceptionResolver;

import java.util.List;

/**
 * ServletFilter를 수동으로 등록한다.
 * 만약 @Component를 부착하여 SpringBean으로 등록하는 경우,
 * Springboot 자동구성으로 인하여 Filter가 ServletFilter로 자동등록된다.
 * Filter를 SpringSecurity의 관리하에 두기 위해서는, 커스텀 Filter를 ServletFilter로 등록되어있는
 * 'SecurityFilterChain'의 하위 필터로 추가해야한다.
 * @apiNote 새로운 필터 CustomFilter를 필터로 등록하기 위해서는, 필터를 빈으로 등록하는 'customFilter'메소드와,
 * customFilter 빈을 자동으로 Servlet Filter로 등록하지 않도록 설정한
 * FilterRegistrationBean&lt;CustomFilter&gt; 를 반환하는 preventRegisterCustomFilter 메소드를 같이 추가해야한다.
 */
@Configuration
public class
FilterBeanConfig {

  /************** JwtAuthorizationFilter ***************
   *****************************************************/
  @Bean
  public JwtAuthorizationFilter jwtAuthorizationFilter(
    TokenUseCase tokenUseCase,
    AuthenticationTokenPrivder authenticationTokenPrivder
  ){
    return new JwtAuthorizationFilter(tokenUseCase, authenticationTokenPrivder);
  }
  @Bean
  public FilterRegistrationBean<JwtAuthorizationFilter> preventRegisterJwtAuthorizationFilter(JwtAuthorizationFilter filter) {
    return preventsFilterAutoRegistration(filter);
  }

  /************** ExceptionCatchFilter ***************
   *****************************************************/
  @Bean
  public ExceptionCatchFilter exceptionCatchFilter(List<HandlerExceptionResolver> resolvers) {
    return new ExceptionCatchFilter(resolvers);
  }
  @Bean
  public FilterRegistrationBean<ExceptionCatchFilter> preventRegisterExceptionCatchFilter(ExceptionCatchFilter filter) {
    return preventsFilterAutoRegistration(filter);
  }

  /************** PrivateUriPretectFilter ***************
   *****************************************************/
  @Bean
  public PrivateUriPretectFilter privateUriPretectFilter() {
    return new PrivateUriPretectFilter();
  }
  @Bean
  public FilterRegistrationBean<PrivateUriPretectFilter> preventRegisterPrivateUriPretectFilter(PrivateUriPretectFilter filter) {
    return preventsFilterAutoRegistration(filter);
  }

  /************** DevOnlyJwtSessionFixFilter ***************
   *****************************************************/
  @Bean
  @Profile({"dev", "test"})
  public DevOnlyJwtSessionFixFilter devOnlyJwtSessionFixFilter(
    AuthenticationTokenPrivder authenticationTokenPrivder,
    SessionUseCase sessionUseCase,
    TokenUseCase tokenUseCase,
    LoadAccountPort loadAccountPort
  ){
    return new DevOnlyJwtSessionFixFilter(
      authenticationTokenPrivder,
      sessionUseCase,
      tokenUseCase,
      loadAccountPort
    );
  }
  @Bean
  @Profile({"dev", "test"})
  public FilterRegistrationBean<DevOnlyJwtSessionFixFilter> preventRegisterDevOnlyJwtSessionFixFilter(DevOnlyJwtSessionFixFilter filter) {
    return preventsFilterAutoRegistration(filter);
  }


  private static <F extends Filter> FilterRegistrationBean<F> preventsFilterAutoRegistration(F filter) {
    FilterRegistrationBean<F> registration = new FilterRegistrationBean<>(filter);
    registration.setEnabled(false); // 필터 등록 비활성화
    return registration;
  }
}
