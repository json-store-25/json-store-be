package deepdive.jsonstore.domain.order.service;

import deepdive.jsonstore.common.exception.CommonException;
import deepdive.jsonstore.domain.order.exception.OrderException;
import deepdive.jsonstore.domain.member.entity.Member;
import deepdive.jsonstore.domain.member.service.MemberValidationService;
import deepdive.jsonstore.domain.order.dto.*;
import deepdive.jsonstore.domain.order.entity.Order;
import deepdive.jsonstore.domain.order.entity.OrderProduct;
import deepdive.jsonstore.domain.order.entity.OrderStatus;
import deepdive.jsonstore.domain.order.repository.OrderRepository;
import deepdive.jsonstore.domain.product.entity.Product;
import deepdive.jsonstore.domain.product.service.ProductStockService;
import deepdive.jsonstore.domain.product.service.ProductValidationService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private ProductValidationService productValidationService;

    @Mock
    private OrderValidationService orderValidationService;

    @Mock
    private MemberValidationService memberValidationService;

    @Mock
    private ProductStockService productStockService;


    @Nested
    @DisplayName("주문 생성")
    class createOrder {

        @Test
        @DisplayName("성공")
        void createOrder_성공() {
            // given
            UUID memberUid = UUID.randomUUID();
            UUID productUid = UUID.randomUUID();

            Member member = Member.builder()
                    .uid(memberUid)
                    .build();

            Product product = Product.builder()
                    .uid(productUid)
                    .price(10000)
                    .stock(10)
                    .build();

            OrderRequest orderRequest = OrderRequest.builder()
                    .orderProductRequests(List.of(
                            OrderProductRequest.builder()
                                    .productUid(productUid)
                                    .quantity(2)
                                    .build()
                    ))
                    .build();

            Order savedOrder = Order.builder()
                    .uid(UUID.randomUUID())
                    .member(member)
                    .orderStatus(OrderStatus.PAYMENT_PENDING)
                    .total(20000)
                    .expiredAt(LocalDateTime.now().plusMinutes(1))
                    .build();

            when(memberValidationService.findById(member.getId())).thenReturn(member); // mock 설정
            when(productValidationService.findActiveProductById(productUid)).thenReturn(product);
            when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);

            // when
            UUID result = orderService.createOrder(member.getId(), orderRequest);

            // then
            assertThat(result).isNotNull();
            assertThat(savedOrder.getUid()).isEqualTo(result);

            verify(orderRepository, times(1)).save(any(Order.class));
            verify(memberValidationService).findById(member.getId());
            verify(productValidationService).findActiveProductById(productUid);
        }

        @Test
        @DisplayName("실패-재고부족")
        void create_재고부족() {
            // given
            UUID memberUid = UUID.randomUUID();
            UUID productUid = UUID.randomUUID();

            Member member = Member.builder()
                    .uid(memberUid)
                    .build();

            Product product = Product.builder()
                    .uid(productUid)
                    .price(10000)
                    .stock(1)
                    .build();

            OrderRequest orderRequest = OrderRequest.builder()
                    .orderProductRequests(List.of(
                            OrderProductRequest.builder()
                                    .productUid(productUid)
                                    .quantity(2)
                                    .build()
                    ))
                    .build();

            when(memberValidationService.findById(member.getId())).thenReturn(member); // mock 설정
            when(productValidationService.findActiveProductById(productUid)).thenReturn(product);

            // when & then
            assertThatThrownBy(()-> orderService.createOrder(member.getId(), orderRequest))
                    .isInstanceOf(OrderException.OrderOutOfStockException.class);

            verify(memberValidationService).findById(member.getId());
            verify(productValidationService).findActiveProductById(productUid);
        }
    }

    @Nested
    @DisplayName("주문 조회")
    class getOrder {

        @Test
        @DisplayName("성공")
        void getOrder_성공() {
            //given
            var orderUid = UUID.randomUUID();
            var member = Member.builder().build();

            var order = Order.builder()
                    .uid(orderUid)
                    .member(member)
                    .orderStatus(OrderStatus.PAYMENT_PENDING)
                    .expiredAt(LocalDateTime.now().plusMinutes(15))
                    .build();

            //when
            when(orderValidationService.findByUid(orderUid)).thenReturn(order);

            //then
            var orderResponse = orderService.getOrder(orderUid);

            assertThat(orderResponse.orderUid()).isEqualTo(orderUid);
            verify(orderValidationService, times(1)).findByUid(orderUid);
        }

        @Test
        @DisplayName("실패-만료")
        void getORder_만료() {
            //given
            var orderUid = UUID.randomUUID();
            var member = Member.builder().build();

            var order = Order.builder()
                    .uid(orderUid)
                    .member(member)
                    .orderStatus(OrderStatus.PAYMENT_PENDING)
                    .expiredAt(LocalDateTime.now())
                    .build();
            //when
            when(orderValidationService.findByUid(orderUid)).thenReturn(order);

            //then
            assertThatThrownBy(() -> orderService.getOrder(orderUid))
                    .isInstanceOf(OrderException.OrderExpiredException.class);

        }
    }

    @Nested
    @DisplayName("컨펌 프로세스")
    class confirmOrder {
        @Test
        @DisplayName("성공")
        void confirmOrder_성공() {
            //given
            var product = Product.builder()
                    .stock(10)
                    .build();

            List<OrderProduct> products = List.of(OrderProduct.builder()
                    .product(product)
                    .quantity(1)
                    .build());

            var order = Order.builder()
                    .id(1L)
                    .expiredAt(LocalDateTime.now().plusMinutes(1))
                    .products(products)
                    .total(100)
                    .build();

            var confirmRequest = ConfirmRequest.builder()
                    .paymentKey(order.getUid().toString())
                    .amount(100L)
                    .build();

            //when
            when(orderValidationService.findByUid(order.getUid())).thenReturn(order);
            //then
//            var reason = orderService.confirmOrder(confirmRequest);
//            assertThat(reason).isEqualTo(ConfirmReason.CONFIRM);
//            verify(orderValidationService, times(1)).findByUid(order.getUid());
        }
    }
}
