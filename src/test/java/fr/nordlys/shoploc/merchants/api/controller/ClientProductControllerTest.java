package fr.nordlys.shoploc.merchants.api.controller;

import fr.nordlys.shoploc.merchants.domain.exception.ShopNotFoundException;
import fr.nordlys.shoploc.merchants.domain.model.Product;
import fr.nordlys.shoploc.merchants.domain.service.ProductService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ClientProductController.class)
class ClientProductControllerTest {

    private static final String URL = "/api/v1/shops/1/products";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void returns_articles_without_stock_details() throws Exception {
        Product product = new Product(5L, 1L, "Baguette", "Pain frais",
                new BigDecimal("1.50"), 12, 5, true);

        when(productService.findPublicByShop(1L)).thenReturn(List.of(product));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Baguette"))
                .andExpect(jsonPath("$[0].description").value("Pain frais"))
                .andExpect(jsonPath("$[0].price").value(1.50))
                .andExpect(jsonPath("$[0].outOfStock").value(false))
                .andExpect(jsonPath("$[0].stock").doesNotExist())
                .andExpect(jsonPath("$[0].lowStockThreshold").doesNotExist());
    }

    @Test
    void shows_out_of_stock_status() throws Exception {
        Product product = new Product(6L, 1L, "Pain", null,
                new BigDecimal("2.00"), 0, 5, true);

        when(productService.findPublicByShop(1L)).thenReturn(List.of(product));

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].outOfStock").value(true));
    }

    @Test
    void returns_404_for_unapproved_shop() throws Exception {
        when(productService.findPublicByShop(1L)).thenThrow(new ShopNotFoundException(1L));

        mockMvc.perform(get(URL))
                .andExpect(status().isNotFound());
    }

    @Test
    void returns_empty_list_when_no_articles() throws Exception {
        when(productService.findPublicByShop(1L)).thenReturn(List.of());

        mockMvc.perform(get(URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }
}
