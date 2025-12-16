package org.zerock.mallapi.dto;

import java.util.ArrayList;
import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductDTO {
    private Long pno;

    private String pname;

    private int price;

    private String pdesc;

    private boolean delflag;

    @Builder.Default
    private List<MultipartFile> files = new ArrayList<>(); // 새로운 상품의 등록과 수정 작업 시에 사용자가 새로운 파일을 업로드 시 사용.

    @Builder.Default
    private List<String> uploadFileNames = new ArrayList<>(); // 업로드가 완료된 파일의 이름만 문자열로 보관한 리스트.

}

/**
 * ProductDTO는 상품의 이름이나, 설명 등과 같은 문자열과 함께 여러 개의 첨부파일을 의미하는 MultipratFile의 리스트를 가지도록 설게한다.
 * 
 */