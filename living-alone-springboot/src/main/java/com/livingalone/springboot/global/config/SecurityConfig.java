package com.livingalone.springboot.global.config;


//import capstone.capstone.global.jwt.entity.TokenProvider;
//import capstone.capstone.global.jwt.exception.JwtAccessDeniedHandler;
//import capstone.capstone.global.jwt.exception.JwtAuthenticationEntryPoint;
import com.livingalone.springboot.global.jwt.entity.TokenProvider;
import com.livingalone.springboot.global.jwt.exception.JwtAccessDeniedHandler;
import com.livingalone.springboot.global.jwt.exception.JwtAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {
    private final TokenProvider tokenProvider;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(TokenProvider tokenProvider, JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.tokenProvider = tokenProvider;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors(AbstractHttpConfigurer::disable);
        http
                // token을 사용하는 방식이기 때문에 csrf를 disable합니다.
                .csrf(AbstractHttpConfigurer::disable)

                .exceptionHandling(exceptionHandling -> exceptionHandling
                        .accessDeniedHandler(jwtAccessDeniedHandler)
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )

//                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
//                        .requestMatchers(
//                                PathRequest.toH2Console()
//                                , new AntPathRequestMatcher("/api/login")
//                                , new AntPathRequestMatcher("/api/sign-up")
//                                , new AntPathRequestMatcher("/checkDuplicateMemberId")
//                                , new AntPathRequestMatcher("/findMemberId"), new AntPathRequestMatcher("/**")
//                        ).permitAll()
//                        .anyRequest().authenticated()
//                )

                .authorizeHttpRequests(authorizeHttpRequests -> authorizeHttpRequests
                        .requestMatchers(
                                new AntPathRequestMatcher("/api/login"),
                                new AntPathRequestMatcher("/api/sign-up"),
                                new AntPathRequestMatcher("/checkDuplicateMemberId"),
                                new AntPathRequestMatcher("/findMemberId"),
                                new AntPathRequestMatcher("/**")
                        ).permitAll()
                        .anyRequest().authenticated()
                )

                // 세션을 사용하지 않기 때문에 STATELESS로 설정
                .sessionManagement(sessionManagement ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )

                // enable h2-console
                .headers(headers ->
                        headers.frameOptions(HeadersConfigurer.FrameOptionsConfig::sameOrigin)
                )
                .apply(new JwtSecurityConfig(tokenProvider));
        return http.build();
    }
}
