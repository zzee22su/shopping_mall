package com.shop.security;
import com.shop.security.filter.JWTAuthenticationFilter;
import com.shop.security.filter.JWTAuthorizationFilter;
import com.shop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserService userService;

    @Bean
    public BCryptPasswordEncoder getBCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        JWTAuthenticationFilter authenticationFilter = new JWTAuthenticationFilter(authenticationManager(), userService);
        authenticationFilter.setFilterProcessesUrl("/api/v1/login");

        JWTAuthorizationFilter authorizationFilter = new JWTAuthorizationFilter(authenticationManager(), userService);

        http.csrf().disable();
        // 세션을 사용하지 않음
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.formLogin().disable();
        http.httpBasic().disable();

        //http.addFilter(corsFilter) // CrossOrigin(인증x) 시큐리티 필터에 등록 인증
        http.addFilter(authenticationFilter) // 로그인 path 호출 시 로그인 인증 하는 필터
                .addFilter(authorizationFilter);  // 요청 url에 따라 접근 제어를 가지는 필터

        http.authorizeRequests().antMatchers("/api/v1/login/**").permitAll(); ///api/v1/login/** 주소 요청 시 로그인 인증 안되도 허용
        http.authorizeRequests().antMatchers("/api/v1/token/refresh/**").permitAll();
        http.authorizeRequests().antMatchers("/api/v1/sign/**").permitAll();
        http.authorizeRequests().antMatchers("/api/v1/logout/**").permitAll();

//        http.authorizeRequests()
//                .antMatchers("/api/v1/user/**")
//                .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN') or hasRole('ROLE_USER')")
//                .antMatchers("/api/v1/manager/**")
//                .access("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')")
//                .antMatchers("/api/v1/admin/**")
//                .access("hasRole('ROLE_ADMIN')");

        //http.authorizeRequests().anyRequest().authenticated(); // 그외에 요청 주소는 인증 필요
    }
}
