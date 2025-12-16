package org.zerock.mallapi.dto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MemberDTO extends User{
    private String email;

    private String pw;

    private String nickName; 

    private boolean social;

    private List<String> roleNames = new ArrayList<>();

    public MemberDTO(String email, String pw, String nickName, boolean social, List<String> roleNames){
        
        super(email, pw, roleNames.stream().map(str -> new SimpleGrantedAuthority("ROLE_" + str)).collect(Collectors.toList()));

        this.email = email;
        this.pw = pw;
        this.nickName = nickName;
        this.social = social;
        this.roleNames =roleNames;
    }

    /*
        현재 사용자의 정보를 Map타입으로 반환하도록 구성하였으며, 
        이는 나중에 JWT 문자열 생성 시에 사용하기 위함이다.
    */
    public Map<String, Object> getClaims(){
        Map<String, Object> dataMap = new HashMap<>();

        dataMap.put("email", email);
        dataMap.put("pw", pw);
        dataMap.put("nickName", nickName);
        dataMap.put("social", social);
        dataMap.put("roleNames", roleNames);

        return dataMap;
    }
    
}

/*
    MemberDTO는 기존의 DTO와는 달리 스프링 시큐리티에서 이용하는 타입의 객체로 만들어서 사용하기 위해서
    Springframework.security.core.userdetails.User클래스를 상속하는 구조로 생성하고, 
    User 클래스의 생성자를 사용할 수 있는 구조로 작성

    주의 사항 (EqualsAndHashCode)
    @Data를 사용하면 자동으로 equals()와 hashCode()가 생성되는데, 상속 관계에 있는 클래스에서는 이 부분이 문제가 될 수 있습니다.
    MemberDTO가 User 클래스(extends User)를 상속하고 있으므로, equals()와 hashCode()가 부모 클래스의 필드를 고려하지 않아 예상치 못한 동작을 할 수 있습니다.
    이러한 경우 @Data 대신 @Getter, @Setter, @ToString만 유지하거나, @Data를 사용하되 @EqualsAndHashCode(callSuper = true)를 함께 사용하는 것이 안전.

 */
