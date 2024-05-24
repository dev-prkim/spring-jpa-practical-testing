package sample.cafekiosk.spring.domain.order;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.cafekiosk.spring.domain.BaseEntity;
import sample.cafekiosk.spring.domain.orderproduct.OrderProduct;
import sample.cafekiosk.spring.domain.product.Product;

@Getter
@NoArgsConstructor(access =  AccessLevel.PROTECTED)
@Table(name = "orders") // order는 예약어이기 때문에 테이블명을 변경해야 한다.
@Entity
public class Order extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private OrderStatus orderStatus;

  private int totalPrice;

  private LocalDateTime registeredDateTime;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
  private List<OrderProduct> orderProduct = new ArrayList<>();


  public Order(List<Product> products, LocalDateTime registeredDateTime) {
    this.orderStatus = OrderStatus.INIT;
    this.totalPrice = calculateTotalPrivate(products);
    this.registeredDateTime = registeredDateTime;
    this.orderProduct = products.stream()
        .map(product -> new OrderProduct(this, product))
        .collect(Collectors.toList());
  }

  public static Order create(List<Product> products, LocalDateTime registeredDateTime) {
    return new Order(products, registeredDateTime);
  }

  private static int calculateTotalPrivate(List<Product> products) {
    return products.stream()
        .mapToInt(Product::getPrice)
        .sum();
  }

}
