package sample.cafekiosk.spring.api.service.order;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static sample.cafekiosk.spring.domain.product.ProductSellingStatus.SELLING;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import sample.cafekiosk.spring.client.mail.MailSendClient;
import sample.cafekiosk.spring.domain.history.mail.MailSendHistoryRepository;
import sample.cafekiosk.spring.domain.order.Order;
import sample.cafekiosk.spring.domain.order.OrderRepository;
import sample.cafekiosk.spring.domain.order.OrderStatus;
import sample.cafekiosk.spring.domain.orderproduct.OrderProductRepository;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductType;

@ActiveProfiles("test")
@SpringBootTest
class OrderStatisticsServiceTest {

  @Autowired
  private OrderStatisticsService orderStatisticsService;

  @Autowired
  private OrderProductRepository orderProductRepository;

  @Autowired
  private OrderRepository orderRepository;

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private MailSendHistoryRepository mailSendHistoryRepository;

  @MockBean
  private MailSendClient mailSendClient;

  @AfterEach
  void tearDown() {
    orderProductRepository.deleteAllInBatch();
    orderRepository.deleteAllInBatch();
    productRepository.deleteAllInBatch();
  }

  @DisplayName("결제완료 주문들을 조회하여 매출 통계 메일을 전송한다.")
  @Test
  void sendOrderStatisticsMail() {
    // given
    LocalDateTime now = LocalDateTime.of(2024, 4, 1, 10, 0);

    Product product1 = createProduct(HANDMADE, "001", 1000);
    Product product2 = createProduct(HANDMADE, "002", 2000);
    Product product3 = createProduct(HANDMADE, "003", 3000);
    productRepository.saveAll(List.of(product1, product2, product3));

    List<Product> products = List.of(product1, product2, product3);
    Order order1 = createPaymentCompletedOrder(LocalDateTime.of(2024, 4, 1, 23, 59), products);
    Order order2 = createPaymentCompletedOrder(now, products);
    Order order3 = createPaymentCompletedOrder(LocalDateTime.of(2024, 4, 3, 23, 59), products);
    Order order4 = createPaymentCompletedOrder(LocalDateTime.of(2024, 4, 4, 0, 0), products);
    orderRepository.saveAll(List.of(order1, order2, order3, order4));

    // mockito 로 stubbing
    when(mailSendClient.sendEmail(any(String.class), any(String.class), any(String.class), any(String.class)))
        .thenReturn(true);

    // when
    boolean result = orderStatisticsService.sendOrderStatisticsMail(LocalDate.of(2024, 4, 1), "test@test.com");

    // then
    assertThat(result).isTrue();
    assertThat(mailSendHistoryRepository.findAll()).hasSize(1)
        .extracting("content")
        .contains("총 매출 합계는 12000원 입니다.");

  }


  private Product createProduct(ProductType type, String productNumber, int price) {
    return Product.builder()
        .type(type)
        .productNumber(productNumber)
        .price(price)
        .sellingStatus(SELLING)
        .name("메뉴 이름")
        .build();
  }

  private Order createPaymentCompletedOrder(LocalDateTime now, List<Product> products) {
    return Order.builder()
        .products(products)
        .orderStatus(OrderStatus.PAYMENT_COMPLETED)
        .registeredDateTime(now)
        .build();
  }

}
