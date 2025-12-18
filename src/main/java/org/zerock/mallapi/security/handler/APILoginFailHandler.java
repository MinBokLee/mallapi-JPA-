package org.zerock.mallapi.security.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class APILoginFailHandler implements AuthenticationFailureHandler{

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {
                
                log.info("Login fail" +  exception);
                Gson gson = new Gson();
                String jsonStr =    gson.toJson(Map.of("error", "ERROR_LOGIN"));

                response.setContentType("application/json");
                PrintWriter printWriter = response.getWriter();
                printWriter.println(jsonStr);
                printWriter.close();        
    }

}
/* 인증실패
    API 서버의 호출 결과 로그인을 할 수 없는 사용자라면 BadCredntialException이 발생하게 된다.
    이에 대한 메시지 열기 JSON 문자열을 생성해서 전송해 주어야 한다.

    AuthhenticationFailureHandler 인터페이스를 구현해서 이를 처리한다.
 */
