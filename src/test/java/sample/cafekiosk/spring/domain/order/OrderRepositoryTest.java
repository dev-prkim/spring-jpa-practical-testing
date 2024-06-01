package sample.cafekiosk.spring.domain.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.HOLD;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;

@ActiveProfiles("test")
@DataJpaTest
class OrderRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private OrderRepository orderRepository;

  @AfterEach
  void tearDown() {
    orderRepository.deleteAllInBatch();
    productRepository.deleteAllInBatch();
  }

  @DisplayName("주어진 기간 내 결제완료된 주문 리스트를 조회한다.")
  @Test
  void test() {
    // given
    LocalDate today = LocalDate.now();
    LocalDateTime startDateTime = today.atStartOfDay();
    LocalDateTime endDateTime = today.plusDays(1).atStartOfDay();

    Product product1 = createProduct("001", SELLING, "아메리카노", 4000);
    Product product2 = createProduct("002", HOLD, "카페라떼", 4500);
    productRepository.saveAll(List.of(product1, product2));

    Order order1 = createOrder(List.of(product1, product2), today.atStartOfDay(), OrderStatus.PAYMENT_COMPLETED);
    Order order2 = createOrder(List.of(product1), today.atStartOfDay(), OrderStatus.INIT);
    Order order3 = createOrder(List.of(product1), today.plusDays(2).atStartOfDay(), OrderStatus.INIT);
    orderRepository.saveAll(List.of(order1, order2, order3));

    // when
    List<Order> completedOrders = orderRepository.findOrdersBy(startDateTime, endDateTime, OrderStatus.PAYMENT_COMPLETED);

    // then
    assertThat(completedOrders).hasSize(1)
        .extracting("registeredDateTime", "orderStatus", "totalPrice")
        .containsExactlyInAnyOrder(
            tuple(today.atStartOfDay(), OrderStatus.PAYMENT_COMPLETED, 8500)
        );

  }

  private static Product createProduct(String productNumber, ProductSellingStatus sellingStatus, String name, int price) {
    return Product.builder()
        .productNumber(productNumber)
        .type(HANDMADE)
        .sellingStatus(sellingStatus)
        .name(name)
        .price(price)
        .build();
  }

  private static Order createOrder(List<Product> products, LocalDateTime registeredDateTime, OrderStatus orderStatus) {
    return Order.builder()
        .products(products)
        .registeredDateTime(registeredDateTime)
        .orderStatus(orderStatus)
        .build();
  }

}
