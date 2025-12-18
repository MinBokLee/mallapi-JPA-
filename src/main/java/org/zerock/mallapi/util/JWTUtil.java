package org.zerock.mallapi.util;

import java.io.UnsupportedEncodingException;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Map;

import javax.crypto.SecretKey;
import javax.management.RuntimeErrorException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.WeakKeyException;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JWTUtil {
    
    private static String key = "hvqHjUPeEw87mN1KhCB8pmGacDJpfIVAFSueMANxZIg";

    // 토큰 생성
    public static String generateToken(Map<String, Object> valueMap, int min){
        
        SecretKey key = null;

        try {
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        String jwtStr = Jwts.builder()
                            .setHeader(Map.of("typ","jwt"))                                      // 헤더 설정
                            .setClaims(valueMap)                                                        // playload(claims) 설정
                            .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))                    // 발생 시간
                            .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(min).toInstant())) //만료시간
                            .signWith(key)                                                              //서명(Key와 기본 Hs256 알고리즘 사용)
                            .compact();                                                                 // 토큰 압축 및 문자열 반환

        return jwtStr;
    }

    public static Map<String, Object> vailedToken(String token){
        
        Map<String, Object> claim = null;
        
       try {
        SecretKey key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));

        claim = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token) // 파싱 및, 검증 실패 시 에러
                .getBody();
        } catch (MalformedJwtException malformedJwtException) {
            throw new CustomJWTException("MalFormed");
        } catch (ExpiredJwtException expiredJwtException){
            throw new CustomJWTException("Expired");   
        } catch (InvalidClaimException invalidClaimException) {
            throw new CustomJWTException("Invalid");
        }  catch(JwtException JwtException)    {
            throw new CustomJWTException("JWT ERROR");
        } catch (Exception e){
            throw new CustomJWTException("Error");
        }
         
       return claim;     
    }

}

/*
JWT문자열 생성을 우한 generationToken(),
토큰 검증을 휘한 vailedToken()을 작성한다.

JWT 토큰 생성 시, 필요한 암호키를 지정하는데 길이가 짧으면 문제가 생기므로 30 이상의 문자열을 지정하는 것이 좋다.
AccessToken 10분, RefreshToken 24시간 유효.

생성된 토큰은 jwt.io 사이트에서 확인할 수 있다, 
*/
