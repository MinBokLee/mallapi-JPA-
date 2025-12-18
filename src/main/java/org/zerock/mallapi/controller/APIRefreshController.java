package org.zerock.mallapi.controller;

import java.text.SimpleDateFormat;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.mallapi.util.CustomJWTException;
import org.zerock.mallapi.util.JWTUtil;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@RestController
@RequiredArgsConstructor
@Log4j2
public class APIRefreshController {

    @RequestMapping("/api/member/refresh")
    public Map<String, Object> refresh(@RequestHeader("Authorization") String authHeader, String refreshToken){

        if(refreshToken == null){
            throw new CustomJWTException("NULL_REFRESH");
        }

        if(authHeader == null){
            throw new CustomJWTException("INVALID_STRING");
        }

        String accessToken = authHeader.substring(7);

        //Access Token이 만료되지 않았다면
        if(checkExpiredToken(accessToken) ==false){
            return Map.of("accessToken", accessToken, "refreshToken", refreshToken);
        }

        // Refresh Token 검증
        Map<String, Object> claims = JWTUtil.vailedToken(refreshToken);

        log.info("claims : "  + claims );

        String newAccessToken = JWTUtil.generateToken(claims, 10);

        log.info("exp ; " + claims.get("exp"));

        String newRefreshToken = checkTime((Integer) claims.get("exp")) == true ? JWTUtil.generateToken(claims, 60*24) : refreshToken ;

        return Map.of("accessToken", newAccessToken, "refreshToken", newRefreshToken);

    }

    // 시간이 1시간 미만으로 남았다면
    private boolean checkTime(Integer exp){

        //JWT exp를 날짜로 변환
        java.util.Date expDate = new java.util.Date( (long) exp * (1000));


        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatteDate =   dateFormat.format(expDate);
        
        log.info("토큰 만료 예정 시각: " + formatteDate);

        //현재 시간과의 차이 계산
        long gap = expDate.getTime() - System.currentTimeMillis();
        
        
        //분단위 계산
        long leftMin = gap / (1000 * 60);

        //1시간도 안남았는지
        return leftMin < 60;
    }

    // Access Token 검증
    private boolean checkExpiredToken(String token){
        
        try {
        JWTUtil.vailedToken(token);
        } catch (Exception ex) {
            if(ex.getMessage().equals("Expired"));
            return true;
        }
            return false;
    }

}
/*
    Access Token은 일반적으로 짧은 유효시간을 지정해서 탈취당하더라도 위험을 줄일 수 있도록 구성된다.
    그 때문에, 일반적으로 Access Token이 만료되면 사용자는 Refresh Token을 활용해서,
    새로운 Access Token을 발급받을 수 있는 기능을 같이 사용하는 경우가 많다.

    '/api/member/refresh' 경로를 통해 Access Token과, Refresh Token을 검증하고,
    Access Token이 만료되었고, Refresh Token이 만료되지 않았다면,
    새로운 Access Token을 전송해 주는 기능을 구현한다.

    구현하려는 조건은 다음과 같은 조건들을 만족해야 한다.
    - Access Token이 없거나 잘못된 JWT인 경우 -> 예외 메시지 발생
    
    - Access Token의 유효기간이 남아있는 경우  -> 전달된 토큰을 그대로 전송
        -> Token의 재발행 조건에 해당하지 않으므로, 별도의 처리 없이 전달 받은 Access Token과, Refresh Token을 다시 반환하도록 한다.
    
    - Access Token은 만료, Refresh Token은 만료되지 않은 경우 -> 새로운 Access Token 
    
    - Refresh Token의 유효기간이 얼마 남지 않은 경우 - > 새로운 Refresh Token 
    
    - Refresh Token의 유효기간이 충분히 남은 경우    - > 기존의 Refresh Token 
*/
