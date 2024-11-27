package com.popflix.global.config;

import com.popflix.global.filter.JwtFilter;
import com.popflix.global.jwt.JwtUtil;
import com.popflix.auth.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final RefreshTokenService refreshTokenService;

    @Bean
    public JwtFilter jwtFilter() {
        return new JwtFilter(jwtUtil);
    }

    @Bean
    public org.springframework.security.web.SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf().disable() // csrf 비활성화
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS) // 세션 관리 안 함
                .and()
                .authorizeRequests()
                .requestMatchers("/auth/**", "/oauth2/**").permitAll() // 인증 없이 접근 허용
                .anyRequest().authenticated() // 나머지 요청은 인증 필요
                .and()
                .addFilterBefore(jwtFilter(), UsernamePasswordAuthenticationFilter.class); // JWT 필터 추가
        return http.build();
    }
}