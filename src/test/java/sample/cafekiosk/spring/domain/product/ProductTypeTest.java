package sample.cafekiosk.spring.domain.product;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductTypeTest {

  @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
  @Test
  void containsStockType() {
    // given
    ProductType givenType = ProductType.HANDMADE;

    // when
    boolean result = ProductType.containsStockType(givenType);

    // then
    assertThat(result).isFalse();
  }

  @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
  @Test
  void containsStockType2() {
    // given
    ProductType givenType = ProductType.BAKERY;

    // when
    boolean result = ProductType.containsStockType(givenType);

    // then
    assertThat(result).isTrue();
  }


  @DisplayName("상품 타입이 재고 관련 타입인지를 체크한다.")
  @Test
  void containsStockType3() {
    // given
    ProductType givenType = ProductType.BOTTLE;

    // when
    boolean result = ProductType.containsStockType(givenType);

    // then
    assertThat(result).isTrue();
  }

}
