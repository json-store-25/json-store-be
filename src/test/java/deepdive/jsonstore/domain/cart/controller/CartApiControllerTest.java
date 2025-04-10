package deepdive.jsonstore.domain.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import deepdive.jsonstore.domain.cart.dto.CartDeleteRequest;
import deepdive.jsonstore.domain.cart.dto.CartRequest;
import deepdive.jsonstore.domain.cart.dto.CartResponse;
import deepdive.jsonstore.domain.cart.entity.Cart;
import deepdive.jsonstore.domain.cart.service.CartService;
import deepdive.jsonstore.domain.member.entity.Member;
import deepdive.jsonstore.domain.product.entity.Product;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
class CartApiControllerTest {

    private MockMvc mockMvc;

    @Mock
    private CartService cartService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // @Mock 초기화
        CartApiController controller = new CartApiController(cartService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Nested
    @DisplayName("addProductToCart API 테스트")
    class AddProductToCart {

        @Test
        @DisplayName("성공")
        void success() throws Exception {
            // given
            CartRequest request = new CartRequest(1L, 10L, 3L);

            Cart mockCart = Cart.builder()
                    .id(100L)
                    .member(Member.builder().id(1L).build())
                    .product(Product.builder().id(10L).build())
                    .amount(3L)
                    .build();

            Mockito.when(cartService.addProductToCart(anyLong(), anyLong(), anyLong()))
                    .thenReturn(mockCart);

            // when & then
            mockMvc.perform(post("/api/v1/carts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.id").value(100L))
                    .andExpect(jsonPath("$.amount").value(3));
        }

        @Test
        @DisplayName("실패 - 유효하지 않은 요청 (memberId 없음)")
        void fail_invalidRequest() throws Exception {
            // given: memberId 누락
            String invalidJson = """
                {
                    "productId": 10,
                    "amount": 2
                }
            """;

            // when & then
            mockMvc.perform(post("/api/v1/carts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson))
                    .andExpect(status().isBadRequest());
        }
    }

    @Nested
    @DisplayName("deleteCartByMemberId API 테스트")
    class DeleteCartByMemberId {

        @Test
        @DisplayName("성공 - 장바구니 항목 삭제")
        void success() throws Exception {
            // given
            CartDeleteRequest request = new CartDeleteRequest(5L); // 예시 cartId

            // when & then
            mockMvc.perform(delete("/api/v1/carts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isNoContent());

            Mockito.verify(cartService).deleteCartByCartId(5L);
        }

        @Test
        @DisplayName("실패 - 유효하지 않은 요청 (cartId 누락)")
        void fail_invalidRequest() throws Exception {
            // given: cartId 빠진 요청
            String invalidJson = "{}";

            // when & then
            mockMvc.perform(delete("/api/v1/carts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(invalidJson));

            Mockito.verify(cartService, never()).deleteCartByCartId(anyLong());
        }
    }

    @Nested
    @DisplayName("getCartByMemberId API 테스트")
    class GetCartByMemberId {

        @Test
        @DisplayName("성공 - 장바구니 목록 조회")
        void success() throws Exception {
            // given
            Cart cart = Cart.builder()
                    .id(1L)
                    .member(Member.builder().id(1L).build())
                    .product(Product.builder().id(10L).build())
                    .amount(2L)
                    .build();

            Mockito.when(cartService.getCartByMemberId(anyLong()))
                    .thenReturn(List.of(cart));

            // when & then
            mockMvc.perform(get("/api/v1/carts")
                            .param("memberId", "1")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$", hasSize(1)))
                    .andExpect(jsonPath("$[0].id").value(1L))
                    .andExpect(jsonPath("$[0].amount").value(2));

            Mockito.verify(cartService).getCartByMemberId(1L);
        }

        @Test
        @DisplayName("실패 - 유효하지 않은 요청 (memberId 누락)")
        void fail_invalidRequest() throws Exception {
            // when & then
            mockMvc.perform(get("/api/v1/carts")
                            .accept(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }
    }
}
