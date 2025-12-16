package org.zerock.mallapi.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// 서비스 계층의 파라미터와 리턴 타입은 DTO를 많이 이용하지만 내부적으로는 엔티티 객체를 사용해야 하는 경우가 많이 때문에 
// DTO <-> 엔티티 처리를 수월하게 할 수 있는 ModelMapper를 활용하는 것이 편리함.


@Configuration
public class RootConfig {

    @Bean
    public ModelMapper getMapper(){
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                    .setFieldMatchingEnabled(true)
                    .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                    .setMatchingStrategy(MatchingStrategies.LOOSE);

        return modelMapper;
    }

}
