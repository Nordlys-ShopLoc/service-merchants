package fr.nordlys.shoploc.merchants.api.controller;

import fr.nordlys.shoploc.merchants.domain.exception.MerchantHasNoShopException;
import fr.nordlys.shoploc.merchants.domain.exception.OperationNotAllowedException;
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

/** US-S04: route /api/v1/shops/mine/stock-alerts. */
@WebMvcTest(StockAlertController.class)
class StockAlertControllerTest {

    private static final String URL = "/api/v1/shops/mine/stock-alerts";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductService productService;

    @Test
    void lists_the_stock_alerts_with_their_level() throws Exception {
        when(productService.findStockAlerts(10L, "MERCHANT")).thenReturn(List.of(
                new Product(3L, 1L, "Brioche", null, new BigDecimal("4.00"), 0, 0, true),
                new Product(2L, 1L, "Croissant", null, new BigDecimal("1.10"), 4, 10, true)));

        mockMvc.perform(get(URL).header("X-User-Id", "10").header("X-User-Role", "MERCHANT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].level").value("OUT_OF_STOCK"))
                .andExpect(jsonPath("$[1].level").value("LOW_STOCK"))
                .andExpect(jsonPath("$[1].lowStockThreshold").value(10));
    }

    @Test
    void answers_403_to_a_client() throws Exception {
        when(productService.findStockAlerts(10L, "CLIENT"))
                .thenThrow(new OperationNotAllowedException("Only a merchant can manage a shop"));

        mockMvc.perform(get(URL).header("X-User-Id", "10").header("X-User-Role", "CLIENT"))
                .andExpect(status().isForbidden());
    }

    @Test
    void answers_404_to_a_merchant_without_shop() throws Exception {
        when(productService.findStockAlerts(10L, "MERCHANT")).thenThrow(new MerchantHasNoShopException(10L));

        mockMvc.perform(get(URL).header("X-User-Id", "10").header("X-User-Role", "MERCHANT"))
                .andExpect(status().isNotFound());
    }
}