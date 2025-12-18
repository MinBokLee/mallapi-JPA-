package org.zerock.mallapi.security;

import java.util.stream.Collectors;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.zerock.mallapi.domain.Member;
import org.zerock.mallapi.dto.MemberDTO;
import org.zerock.mallapi.repository.MemberRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

@Service
@Log4j2
@RequiredArgsConstructor
public class CustomUserDetailService implements UserDetailsService{
    
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("---------------loadUserByUsername-----------------");
        
        Member member = memberRepository.getWithRols(username);
            
        if(member == null){
            throw new UsernameNotFoundException("not found");
        }
        
        MemberDTO memberDTO = new MemberDTO(
                                member.getEmail(),
                                member.getPw(),
                                member.getNickName(),
                                member.isSocial(),
                                member.getMemberRoleList()
                                .stream()
                                .map(memberRole -> memberRole.name()).collect(Collectors.toList()));

                                log.info("memberDTO ", memberDTO);
        

        return memberDTO;
    }
} //end of loadUSerByUsername

/*
   스프링 시큐리티는 사용자의 인증 처리를 위해서 UserDetailService라는 인터페이스의 구현체를 활용한다. 
   시큐리티를 적용하면 CustomUserDetailService의 loadUserByUserName()에서 사용자 정보를 조회하고, 
   해당 사용자의 인증과 권한을 처리한다. API서버로 로그인할 수 있도록 CustomSecurityConfig의 설정을 변경한다.
 */
