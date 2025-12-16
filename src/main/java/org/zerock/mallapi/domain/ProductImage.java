package org.zerock.mallapi.domain;

import jakarta.persistence.Embeddable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @@Embeddable를 이용해서 해당 클래스의 인스턴스가 값 타임 객체임을 명시.
 * ord (순서) 라는 속성을 가지도록 만드는데, 이것은 나중에 목록에서 각 이미지마다 번호를 지정하고 상품 목록을 출력할 때 
 * odf 값이 0번인 이미지들만 화면에서만 볼 수 있도록 하기 위함.(대표이미지만 출력하고자 하는 경우에 사용)
 */

@Embeddable
@Getter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductImage {

    private String fileName;

    private int ord;

    void setOrd(int ord){
        this.ord = ord;
    }
}

