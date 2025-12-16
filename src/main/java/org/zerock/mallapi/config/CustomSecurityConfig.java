package org.zerock.mallapi.config;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Arrays;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

//보안구성
@Configuration
@Log4j2
@RequiredArgsConstructor
public class CustomSecurityConfig {

    @Bean 
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

        log.info("----------security config-----------");
        http.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(configurationSource());
        });

        http.sessionManagement(sessionConfig -> sessionConfig.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
        http.csrf(config -> config.disable());

        http.formLogin(config -> {
            config.loginPage("/api/member/login");
        });
        //formLogin 설정을 추가하면 스프링시큐리티는 POST방식으로 username과 password라는 파라미터를 통해서 로그인을 처리할 수 있게 된다.

        return http.build();

    }

    @Bean
    public CorsConfigurationSource configurationSource(){
       
        CorsConfiguration configuration = new CorsConfiguration();
            
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content=Type"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
       
        return source;
    }


/*
 *  @Configuration 클래스에서 **SecurityFilterChain 빈(Bean)**을 정의하여 Spring Security의 동작 방식을 설정합니다.
     인증 (Authentication): 사용자 자격 증명(ID/PW)을 확인하고 사용자가 누구인지 식별합니다. (로그인 처리)

    인가 (Authorization): 인증된 사용자가 특정 URL (/admin 등)이나 메서드에 접근할 권한이 있는지 확인합니다.
    CSRF 방어: 웹 사이트 요청 위조 공격을 방어하기 위한 토큰 설정을 합니다.

    세션 관리: 사용자 상태를 유지하고 세션 관련 보안 정책을 적용합니다.
    요약하자면,
    CustomServletConfig는 HTTP 요청을 어떻게 받아들이고(CORS), 처리하며(Interceptor), 응답을 어떻게 만들지(View Resolver)를 정의합니다.
    CustomSecurityConfig는 그 HTTP 요청이 사용자에게 허용될 수 있는 요청인지 아닌지(인증/인가)를 판단하는 '보안 검문소'의 역할을 정의합니다.
 */

//   Bean
//     public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        
//         // 1. 접근 권한 설정
//         http
//             .authorizeHttpRequests((auth) -> auth
//                 .antMatchers("/public/**").permitAll()   // 모두 허용
//                 .anyRequest().authenticated()           // 나머지는 인증 필요
//             );

//         // 2. HTTP 설정 구성 (예: CSRF 비활성화)
//         http.csrf().disable(); 

//         // 3. 필터 체인 객체 반환
//         return http.build();
//     }



}
