package org.zerock.mallapi.security.handler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CustomAccessDeniedHandler implements AccessDeniedHandler{

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
            AccessDeniedException accessDeniedException) throws IOException, ServletException {
        
                Gson gson = new Gson();
                String jsonStr =  gson.toJson(Map.of("error", "ERROR_ACCESSDEIND"));
                
                response.setContentType("application/json");
                response.setStatus(HttpStatus.FORBIDDEN.value());
                PrintWriter printWriter = response.getWriter();
                printWriter.println(jsonStr);
                printWriter.close();
    }
}

/*
    API 서버는 항상 호출한 애플리케이션에게 정확한 메시지를 전달해야 하므로, 
    접근제한 상황에 대해서 예외 메시지를 전달해 주어야 한다.

    이를 위해서 security/handler 패키지에 CustomAccessDeniedHandler클래스를 추가한다.
    CustomSecurityConfig 에서는 접근 제한 시, CustomAccessDeniedHandler를 활용하도록 설정을 추가한다.

    token 유효시간이 지나지 않았지만 권한이 없는 사용자가 가진 Access Token을 사용하는 경우 
    error: ERROR_ACCESSDENIED 와 같은 메시지를 전송한다.

*/