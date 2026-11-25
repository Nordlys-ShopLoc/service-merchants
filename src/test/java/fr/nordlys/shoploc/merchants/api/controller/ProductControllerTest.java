package fr.nordlys.shoploc.merchants.api.controller;

import fr.nordlys.shoploc.merchants.domain.exception.OperationNotAllowedException;
import fr.nordlys.shoploc.merchants.domain.exception.ProductNotFoundException;
import fr.nordlys.shoploc.merchants.domain.model.Product;
import fr.nordlys.shoploc.merchants.domain.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** US-S03: routes /api/v1/shops/mine/products. */
@WebMvcTest(ProductController.class)
class ProductControllerTest {

    private static final String URL = "/api/v1/shops/mine/products";
    private static final String VALID_JSON = """
            {"name": "Pain de seigle", "description": null, "price": 2.50, "stock": 12}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    private static Product article(boolean available) {
        return new Product(5L, 1L, "Pain de seigle", null, new BigDecimal("2.50"), 12, 0, available);
    }

    @Test
    void lists_the_articles_of_the_merchant() throws Exception {
        when(productService.findMine(10L, "MERCHANT")).thenReturn(List.of(article(true)));

        mockMvc.perform(get(URL).header("X-User-Id", "10").header("X-User-Role", "MERCHANT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Pain de seigle"))
                .andExpect(jsonPath("$[0].available").value(true));
    }

    @Test
    void adds_an_article() throws Exception {
        when(productService.add(any(), any(), any(), any(), any(), any())).thenReturn(article(true));

        mockMvc.perform(post(URL).header("X-User-Id", "10").header("X-User-Role", "MERCHANT")
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_JSON))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void refuses_a_price_of_zero() throws Exception {
        mockMvc.perform(post(URL).header("X-User-Id", "10").header("X-User-Role", "MERCHANT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Pain\", \"price\": 0}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(productService);
    }

    @Test
    void refuses_an_article_without_name() throws Exception {
        mockMvc.perform(post(URL).header("X-User-Id", "10").header("X-User-Role", "MERCHANT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"price\": 2.50}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(productService);
    }

    @Test
    void refuses_a_negative_stock() throws Exception {
        mockMvc.perform(post(URL).header("X-User-Id", "10").header("X-User-Role", "MERCHANT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\": \"Pain\", \"price\": 2.50, \"stock\": -1}"))
                .andExpect(status().isBadRequest());
        verifyNoInteractions(productService);
    }

    @Test
    void updates_an_article() throws Exception {
        when(productService.update(any(), any(), any(), any(), any(), any(), any())).thenReturn(article(true));

        mockMvc.perform(put(URL + "/5").header("X-User-Id", "10").header("X-User-Role", "MERCHANT")
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(12));
    }

    @Test
    void withdraws_an_article() throws Exception {
        when(productService.withdraw(10L, "MERCHANT", 5L)).thenReturn(article(false));

        mockMvc.perform(delete(URL + "/5").header("X-User-Id", "10").header("X-User-Role", "MERCHANT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.available").value(false));
    }

    @Test
    void answers_404_for_an_article_of_another_shop() throws Exception {
        when(productService.withdraw(10L, "MERCHANT", 99L)).thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(delete(URL + "/99").header("X-User-Id", "10").header("X-User-Role", "MERCHANT"))
                .andExpect(status().isNotFound());
    }

    @Test
    void answers_403_to_a_user_who_is_not_a_merchant() throws Exception {
        when(productService.findMine(7L, "CLIENT"))
                .thenThrow(new OperationNotAllowedException("Only a merchant can manage a shop"));

        mockMvc.perform(get(URL).header("X-User-Id", "7").header("X-User-Role", "CLIENT"))
                .andExpect(status().isForbidden());
    }

    @Test
    void marks_an_article_out_of_stock() throws Exception {
        Product outOfStock = new Product(5L, 1L, "Pain de seigle", null, new BigDecimal("2.50"), 0, 0, true);
        when(productService.markOutOfStock(10L, "MERCHANT", 5L)).thenReturn(outOfStock);

        mockMvc.perform(post(URL + "/5/out-of-stock").header("X-User-Id", "10").header("X-User-Role", "MERCHANT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(0))
                .andExpect(jsonPath("$.available").value(true));
    }

    @Test
    void answers_404_when_marking_out_of_stock_an_article_of_another_shop() throws Exception {
        when(productService.markOutOfStock(10L, "MERCHANT", 99L)).thenThrow(new ProductNotFoundException(99L));

        mockMvc.perform(post(URL + "/99/out-of-stock").header("X-User-Id", "10").header("X-User-Role", "MERCHANT"))
                .andExpect(status().isNotFound());
    }

    @Test
    void answers_403_when_a_non_merchant_marks_an_article_out_of_stock() throws Exception {
        when(productService.markOutOfStock(7L, "CLIENT", 5L))
                .thenThrow(new OperationNotAllowedException("Only a merchant can manage a shop"));

        mockMvc.perform(post(URL + "/5/out-of-stock").header("X-User-Id", "7").header("X-User-Role", "CLIENT"))
                .andExpect(status().isForbidden());
    }
}
