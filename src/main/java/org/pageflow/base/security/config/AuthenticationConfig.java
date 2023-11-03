package org.pageflow.base.security.config;


import org.pageflow.base.security.filter.InsufficientAuthenticationProcessingFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AuthenticationConfig {


    
    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(UserDetailsService userDetailsService) {
        
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);

        
        /*
        * 기본값은 true -> 이경우 UsernameNotFoundException의 발생이 발생할 경우 이를 진압하고 BadCredentialsException을 대신 발생시킨다.
        * 따라서 이를 따로 설정해주어서 UsernameNotFoundException이 온전히 발생하도록 한다.
        * */
        provider.setHideUserNotFoundExceptions(false);
        provider.setPasswordEncoder(new BCryptPasswordEncoder());
        
        return provider;
    }
    
    @Bean
    InsufficientAuthenticationProcessingFilter insufficientAuthenticationProcessingFilter(){
        return new InsufficientAuthenticationProcessingFilter();
    }
    
    
}
