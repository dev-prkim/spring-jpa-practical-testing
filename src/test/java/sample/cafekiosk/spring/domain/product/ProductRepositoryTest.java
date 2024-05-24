package sample.cafekiosk.spring.domain.product;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.tuple;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.HOLD;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.STOP_SELLING;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
//@SpringBootTest
@DataJpaTest  // JPA 관련된 Beans만 로딩을 하기 때문에 속도가 비교적 빠르다.
class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

  @DisplayName("원하는 판매상태를 가진 상품들을 조회한다.")
  @Test
  void findAllBySellingStatusIn() {
    // given
    Product product1 = Product.builder()
        .productNumber("001")
        .type(HANDMADE)
        .sellingStatus(SELLING)
        .name("아메리카노")
        .price(4000)
        .build();
    Product product2 = Product.builder()
        .productNumber("002")
        .type(HANDMADE)
        .sellingStatus(HOLD)
        .name("카페라떼")
        .price(4500)
        .build();
    Product product3 = Product.builder()
        .productNumber("003")
        .type(HANDMADE)
        .sellingStatus(STOP_SELLING)
        .name("팥빙수")
        .price(7000)
        .build();
    productRepository.saveAll(List.of(product1, product2, product3));

    // when
    List<Product> products = productRepository.findAllBySellingStatusIn(List.of(SELLING, HOLD));

    // then
    assertThat(products).hasSize(2)
        .extracting("productNumber", "name", "sellingStatus") // 검증하고자하는 필드만 추출할 수 있다.
        .containsExactlyInAnyOrder( // 순서에 상관없이 값이 일치하는지 확인한다.
            tuple("001", "아메리카노", SELLING),
            tuple("002", "카페라떼", HOLD)
        );
  }

  @DisplayName("상품번호 리스트로 상품들을 조회한다.")
  @Test
  void findAllByProductNumberIn() {
    // given
    Product product1 = Product.builder()
        .productNumber("001")
        .type(HANDMADE)
        .sellingStatus(SELLING)
        .name("아메리카노")
        .price(4000)
        .build();
    Product product2 = Product.builder()
        .productNumber("002")
        .type(HANDMADE)
        .sellingStatus(HOLD)
        .name("카페라떼")
        .price(4500)
        .build();
    Product product3 = Product.builder()
        .productNumber("003")
        .type(HANDMADE)
        .sellingStatus(STOP_SELLING)
        .name("팥빙수")
        .price(7000)
        .build();
    productRepository.saveAll(List.of(product1, product2, product3));

    // when
    List<Product> products = productRepository.findAllByProductNumberIn(List.of("001", "002"));

    // then
    assertThat(products).hasSize(2)
        .extracting("productNumber", "name", "sellingStatus") // 검증하고자하는 필드만 추출할 수 있다.
        .containsExactlyInAnyOrder( // 순서에 상관없이 값이 일치하는지 확인한다.
            tuple("001", "아메리카노", SELLING),
            tuple("002", "카페라떼", HOLD)
        );
  }

}
