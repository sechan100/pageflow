package org.pageflow.global.config;

import jakarta.servlet.Filter;
import org.pageflow.boundedcontext.auth.adapter.in.web.JwtAuthorizationFilter;
import org.pageflow.boundedcontext.auth.port.in.TokenUseCase;
import org.pageflow.global.filter.ApiExceptionCatchAndDelegatingFilter;
import org.pageflow.global.filter.DevOnlyJwtSessionFixFilter;
import org.pageflow.global.filter.InFilterForwardedRequestCeaseFilter;
import org.pageflow.global.filter.InternalOnlyUriPretectFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * 수동 설정을 트리거하여, Springboot의 자동구성으로 인해 @Component인 Filter가 ServletFilter로 자동등록되는 것을 막는다.
 * 자동등록하지 않은 필터는 SecurityFilterChain에 명시적으로 세팅하여 관리한다.
 */
@Configuration
public class FilterBeanConfig {


    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter(TokenUseCase tokenUseCase) {
        return new JwtAuthorizationFilter(tokenUseCase);
    }
    @Bean
    public FilterRegistrationBean<JwtAuthorizationFilter> preventRegisterJwtAuthorizationFilter(JwtAuthorizationFilter filter) {
        return preventsFilterAutoRegistration(filter);
    }


    @Bean
    public ApiExceptionCatchAndDelegatingFilter throwableDelegatingFilter() {
        return new ApiExceptionCatchAndDelegatingFilter();
    }
    @Bean
    public FilterRegistrationBean<ApiExceptionCatchAndDelegatingFilter> preventRegisterThrowableDelegatingFilter(ApiExceptionCatchAndDelegatingFilter filter) {
        return preventsFilterAutoRegistration(filter);
    }


    @Bean
    public InFilterForwardedRequestCeaseFilter inFilterForwardedRequestCeaseFilter() {
        return new InFilterForwardedRequestCeaseFilter();
    }
    @Bean
    public FilterRegistrationBean<InFilterForwardedRequestCeaseFilter> registrationBeanInFilterForwardedRequestCeaseFilter(InFilterForwardedRequestCeaseFilter filter) {
        return preventsFilterAutoRegistration(filter);
    }


    @Bean
    public InternalOnlyUriPretectFilter internalOnlyUriPretectFilter() {
        return new InternalOnlyUriPretectFilter();
    }
    @Bean
    public FilterRegistrationBean<InternalOnlyUriPretectFilter> preventRegisterInternalOnlyUriPretectFilter(InternalOnlyUriPretectFilter filter) {
        return preventsFilterAutoRegistration(filter);
    }

    @Bean
    @Profile("dev")
    public DevOnlyJwtSessionFixFilter devOnlyJwtSessionFixFilter() {
        return new DevOnlyJwtSessionFixFilter();
    }
    @Bean
    @Profile("dev")
    public FilterRegistrationBean<DevOnlyJwtSessionFixFilter> preventRegisterDevOnlyJwtSessionFixFilter(DevOnlyJwtSessionFixFilter filter) {
        return preventsFilterAutoRegistration(filter);
    }




    private static <F extends Filter> FilterRegistrationBean<F> preventsFilterAutoRegistration(F filter) {
        FilterRegistrationBean<F> registration = new FilterRegistrationBean<>(filter);
        registration.setEnabled(false); // 필터 등록 비활성화
        return registration;
    }
}
