package org.zerock.mallapi.repoistory;

import static org.mockito.ArgumentMatchers.isNull;

import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.AutoClose;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.zerock.mallapi.domain.Product;
import org.zerock.mallapi.repository.ProductRepository;

import lombok.extern.log4j.Log4j2;

@SpringBootTest
@Log4j2
public class ProductRepositoryTests {

    @Autowired
    ProductRepository productRespository;

    /**
     * @ElementCollection과 같이 하나의 엔티티가 여러 개의 객체를 추가적으로 담고 있을 때는,
     * 자동으로 이에 해당하는 테이블이 생성되고 외래키(FK)가 생성된다.
     */
    @Test
    public void testInsert(){
        for(int i = 0; i<10; i++){
            Product product = Product.builder()
                                .pname("상품" + i)
                                .price(100*i)
                                .pdesc("상품설명" + i)
                                .build();
        
        
        // 2개의 이미지 파일 추가
        product.addImageString(UUID.randomUUID().toString() + "_" + "IMAGE1.jpg");
        product.addImageString(UUID.randomUUID().toString() + "_" + "IMAGE1.jpg");

        productRespository.save(product);
        log.info("--------------------");
        }
        
    }

    /**
     * testInsert()는 하나의 Productdp 2개의 첨부파일이 있는 상태로 엔티티를 생성한다.
     * 테스트 코드를 실행하면 데이터베이스 내에 테이블의 생성과 데이터가 추가되었는지 확인할 수 있다.
     * 
     * DB내에는 tbl_product 테이블과, product_image_list 테이블이 생성되는데, 
     * tbl_product는 1개의 상푸마다 두 개의 이미지 파일 데이터를 가지게 된다.
     */ 


    /**
     * 조회
     * 엔티티로는 Product라는 하나의 엔티티 객체지만, 테이블에서는 2개의 테이블로 구성되기 떄문에, 
     * JPA에서 이를 처리할 때 한 번에 모든 테이블을 같이 로딩해서 처리할 것인지 ,필요한 테이블만 먼저 조회할 것인지를 결정할 필요가 있다.
     * Product 엔티티 테이블 구성에 사용한 @ElementCollection은 기본적으로 lazy loading 방식으로 동작하기 때문에, 
     * 우선은 tbl_product 테이블만 접근해서 데이터를 처리하고, 첨부파일이 필요할 때 Product_image_list 테이블을 접근하게 된다.
     * 이처럼 BD에 두 번 접근해서 처리해야 하므로 테스트 코드에는 @Transactional을 적용해야 한다. 
     */


    @Transactional
    @Test
    public void testRead(){
        Long pno = 1L;

        Optional<Product> result = productRespository.findById(pno);

        Product product =  result.orElseThrow();

        log.info(product); //-----------1
        log.info(product.getImageList()); //-------------2
    }


    @Test
    public void testRead2(){
        Long pno = 1L;

        Optional<Product> result = productRespository.selectOne(pno);

        Product product = result.orElseThrow();

        log.info(product);
        log.info(product.getImageList());
    }

    @Commit
    @Transactional
    @Test
    public void updateToDelete(){
        Long pno = 3L;

        productRespository.updateToDelete(pno, false);
    }

    @Test
    public void testUpdate(){
        Long pno = 10L;

        Product product = productRespository.selectOne(pno).get();

        product.changeName("10번 상품");
        product.changeName("10번 상품 설명 입니다");
        product.changePrice(5000);

        product.clearList();

        product.addImageString((UUID.randomUUID().toString() + "_"+"default"));
        product.addImageString((UUID.randomUUID().toString() + "_"+"default2"));
        product.addImageString((UUID.randomUUID().toString() + "_"+"default2"));

    }

    @Test
    public void testlist(){

        Pageable pageable = PageRequest.of(0, 10, Sort.by("pno").descending());

        Page<Object[]> result = productRespository.selectList(pageable);

        //JAVA UTIL
        result.getContent().forEach(arr -> log.info(Arrays.toString(arr)));
    }

}
