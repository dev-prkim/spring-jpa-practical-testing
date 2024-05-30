package sample.cafekiosk.spring.api.controller.product.request;

import javax.validation.constraints.Max;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import sample.cafekiosk.spring.api.service.product.request.ProductCreateServiceRequest;
import sample.cafekiosk.spring.domain.product.ProductSellingStatus;
import sample.cafekiosk.spring.domain.product.ProductType;

@Getter
@NoArgsConstructor
public class ProductCreateRequest {

  @NotNull(message = "상품 타입은 필수입니다.")
  private ProductType type;

  @NotNull(message = "상품 판매상태는 필수입니다.")
  private ProductSellingStatus sellingStatus;

//  // String name -> 상품 이름은 20자 제한
//  @Max(20) 를 추가하는 것보단,
//  // 도메인 정책에 맞는 특수한 형태의 유효성 검사는 책임 분리를 한다.
//  // 20자 제한은 서비스 레이어 또는 도메인 객체에서 검증을 하는 게 나을 수 있다.
  @NotBlank(message = "상품 이름은 필수입니다.")
//  @NotNull  // "" ,  "   " 통과
//  @NotEmpty // "   " 통과
  private String name;

  @Positive(message = "상품 가격은 양수여야 합니다.")
  private int price;

  @Builder
  private ProductCreateRequest(ProductType type, ProductSellingStatus sellingStatus, String name, int price) {
    this.type = type;
    this.sellingStatus = sellingStatus;
    this.name = name;
    this.price = price;
  }

  public ProductCreateServiceRequest toServiceRequest() {
    return ProductCreateServiceRequest.builder()
        .type(type)
        .sellingStatus(sellingStatus)
        .name(name)
        .price(price)
        .build();
  }
}
