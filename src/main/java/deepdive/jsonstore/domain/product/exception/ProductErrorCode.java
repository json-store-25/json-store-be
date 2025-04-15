package deepdive.jsonstore.domain.product.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProductErrorCode {

	PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "없는 상품이거나, 판매가 중지 되었습니다."),
	PRODUCT_FORBIDDEN(HttpStatus.FORBIDDEN, "해당 상품에 접근할 수 없습니다."),
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부에서 예기치 않은 오류가 발생했습니다.");

	private final HttpStatus httpStatus;
	private final String message;
}
