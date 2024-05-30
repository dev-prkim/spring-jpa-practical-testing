package sample.cafekiosk.spring.api.service.product;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import sample.cafekiosk.spring.api.controller.product.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.domain.product.Product;
import sample.cafekiosk.spring.domain.product.ProductRepository;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;

/**
 * readOnly = true : 읽기전용
 * CRUD 에서 CUD 동작 X : only Read
 * JPA : CUD 스냅샷 저장, 변경감지 X => 성능 향상
 * -
 * CQRS - Command / Query 책임 분리
 * -
 * 조회 시 트랜잭션 어노테이션이 누락되면
 * Slave DB 로 가야하는 트랜젝션이 Master 로 갈 수 있기 때문에
 * 클래스 상단에 ReadOnly true 를 걸고,
 * Command 용 메서드에 ReadOnly false(default) 를 걸 수 있다.
 * -
 * 조회용 서비스를 분리하는 것도 좋은 방법이다.
 */
@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class ProductService {

  private final ProductRepository productRepository;

  // 동시성 이슈가 있음
  // 상품번호에 UK 걸고 재시도를 한다거나,
  // 동접자가 너무 많다면 UUID 를 활용할 수 있는 등
  // 여러가지 해결책이 있다.
  @Transactional
  public ProductResponse createProduct(ProductCreateRequest request) {
    String productNumber = createNextProductNumber();

    Product product = request.toEntity(productNumber);
    Product savedProduct = productRepository.save(product);

    return ProductResponse.of(savedProduct);
  }

  public List<ProductResponse> getSellingProducts() {
    List<Product> products = productRepository.findAllBySellingStatusIn(ProductSellingStatus.forDisplay());

    return products.stream()
        .map(ProductResponse::of)
        .collect(Collectors.toList());
  }

  private String createNextProductNumber() {
    String latestProductNumber = productRepository.findLatestProductNumber();
    if(latestProductNumber == null) {
      return "001";
    }

    int lastProductNumberInt = Integer.parseInt(latestProductNumber);
    int nextProductNumberInt = lastProductNumberInt + 1;

    return String.format("%03d", nextProductNumberInt);
  }
}
