package org.zerock.mallapi.security.filter;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Map;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.zerock.mallapi.dto.MemberDTO;
import org.zerock.mallapi.util.JWTUtil;

import com.google.gson.Gson;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class JWTCheckFilter extends OncePerRequestFilter{
    
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        
        // preflight 요청은 체크하지 않음
        if(request.getMethod().equals("OPTIONS")){
            return true;
        }

        String path = request.getRequestURI();

        log.info("check uri " +  path);        

        if(path.startsWith("/api/member/")){
            return true;
        }

        //이미지 조회 경로는 체크하지 않는다면
        if(path.startsWith("/api/products/view/")){
            return true;
        }

        return false;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        
        log.info("-------------- JWTCheckFilter ----------------");

        String authHeaderStr = request.getHeader("Authorization");
        

        try{
            //Bearer accesstoken...
            String accesstoken =  authHeaderStr.substring(7);
            Map<String, Object> claims = JWTUtil.vailedToken(accesstoken);

            log.info("doFilterInternal() JWT Claims: " + claims);
       
            // filterChain.doFilter(request, response); // 통과

            String email    = (String) claims.get("email");
            String pw       = (String) claims.get("pw");
            String nickName = (String) claims.get("nickName");
            Boolean social  = (Boolean) claims.get("social");
            List<String> roleNames = (List<String>) claims.get("roleNames");

            MemberDTO memberDTO = new MemberDTO(email, pw, nickName, social.booleanValue(), roleNames );

            log.info("-----------------------------------------");
            log.info("memberDTO: " + memberDTO );
            log.info("memberDTO.getAuthorities(): " + memberDTO.getAuthorities());

            // 1. 토큰 생성
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberDTO, pw, memberDTO.getAuthorities());

            // 2. SecurityContext에 인증 정보 설정
            SecurityContextHolder.getContext().setAuthentication(authenticationToken);

            // 3. 다음 필터로 이동
            filterChain.doFilter(request, response);

        }catch(Exception e){
            log.error("JWT Check Error..............");
            log.error("Validation Failed: {}", e.getMessage());
            
            // 2. 검증 실패 시 오류 응답 전송
            Gson gson = new Gson();
            String msg = gson.toJson(Map.of("error", "Error_ACCESS_TOKEN"));

            response.setContentType("application/json");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();            
        }
    }
}