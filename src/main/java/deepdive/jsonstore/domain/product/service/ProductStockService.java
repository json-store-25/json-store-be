package deepdive.jsonstore.domain.product.service;

import deepdive.jsonstore.domain.product.entity.Product;
import deepdive.jsonstore.domain.product.exception.ProductException;
import deepdive.jsonstore.domain.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ProductStockService {

    private final ProductRepository productRepository;

    // 리저브 스톡
    @Transactional
    public void reserveStock(Long productId, int quantity) {
        var product = productRepository.findWithLockById(productId)
                .orElseThrow((ProductException.ProductForbiddenException::new));
        product.decreaseStock(quantity);
        if (product.getStock() == 0) {
            // change state
        }
    }

    // 리저브 반환
    public void releaseStock(Long productId, int quantity) {
        var product = productRepository.findWithLockById(productId)
                .orElseThrow((ProductException.ProductForbiddenException::new));
        product.increaseStock(quantity);

        /* 상태가 재고없음인데 릴리즈된다면 상태 변경 */
    }
}
