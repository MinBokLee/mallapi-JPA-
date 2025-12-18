package org.zerock.mallapi.security.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.zerock.mallapi.dto.MemberDTO;
import org.zerock.mallapi.util.JWTUtil;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class APILoginSuccessHandler implements AuthenticationSuccessHandler{

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {
       log.info("-----------------------");
       log.info(authentication);
       log.info("-----------------------");

       MemberDTO memberDTO = (MemberDTO) authentication.getPrincipal();

       Map<String, Object> claims = memberDTO.getClaims();

       String accessToken = JWTUtil.generateToken(claims, 10 ); // 10분
       String refreshToken = JWTUtil.generateToken(claims, 60*24); // 24시간

       claims.put("accesstoken",accessToken);
       claims.put("refreshToken",refreshToken);

       Gson gson = new Gson();
       String jsonStr = gson.toJson(claims);

       response.setContentType("application/json; charset=UTF-8");
        PrintWriter printWriter = response.getWriter();
        printWriter.println(jsonStr);
        printWriter.close();
    }
}
/*
    login에 설공하면 APILoginSuccessHandler를 활용해서 응답 메시지를 생성한다.
    작성하는 응답 메세지는 MemberDTO 안에 있는 getClaims()를 통해서 JSON 데이터를 구성한다.

    
*/

