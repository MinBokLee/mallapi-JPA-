package org.zerock.mallapi.domain;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "tbl_product")
@Getter
@ToString(exclude = "imageList")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pno;

    private String pname;

    private int price;

    private String pdesc;

    private boolean delFlag;

    @ElementCollection
    @Builder.Default
    private List<ProductImage> imageList = new ArrayList<>();

    public void changePrice(int price){
        this.price = price;

    }

    public void changeDesc(String pdesc){
        this.pdesc = pdesc;
    }

    public void changeName(String name){
        this.pname = name;
    }

    public void addImage(ProductImage image){
        image.setOrd(this.imageList.size());
        imageList.add(image);
    }

    public void addImageString(String fileName){

        ProductImage productImage = ProductImage.builder()
                                    .fileName(fileName)
                                    .build();
                                    addImage(productImage);
    }


    public void clearList(){
        this.imageList.clear();
    }

    // 삭제된 상품으로 표시
    public void changDel(boolean delFlag){
        this.delFlag = delFlag;
    }

    public Product orElseThrow() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'orElseThrow'");
    }
}

/**
 * Product에는 문자열로 파일을 추가하거나 ProductImage 타입으로 이미지를 추가할 수 있도록 구성한다.
 * @Embeddable을 사용하는 경우 PK가 생성되지 않기 때문에 모든 작업은 PK를 가지는 엔티티로 구성한다는 특징이 있다.
 * 
 * 상품의 삭제.
 * 실제 DB 내에서 상품의 삭제는 나중에 구매 기록과 이어지기 때문에 주의해야 한다.
 * 예를 들어 특정 상품이 DB에서 삭제되면 해당 상품 데이터를 사용한 모든 구매나 상품 문의 등의 데이터들이 같이 삭제 되어야 한다.
 * 
 * 단순한 예제는 문제가 되지 않겠지만, 실제 서비스 중이라면 통계 데이터나 고객의 리뷰 데이터와 같은 데이터들이 모두 삭제되어야 하기에
 * 심각한 문제가 될 수 있다.
 * 
 * 이에 대한 대안으로 실제 물리적인 삭제 대신에 특정한 컬럼의 값을 기준으로 해당 상품이 삭제되었는지 아닌지를 구분하고, 
 * delete 대신에 update를 이용해서 처리한다.(이러한 논리적인 삭제를 Soft Ddelete) 
 * 
 * */
 

