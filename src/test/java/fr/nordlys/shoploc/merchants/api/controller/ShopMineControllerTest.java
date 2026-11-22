package fr.nordlys.shoploc.merchants.api.controller;

import fr.nordlys.shoploc.merchants.domain.exception.MerchantHasNoShopException;
import fr.nordlys.shoploc.merchants.domain.exception.OperationNotAllowedException;
import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.model.ShopStatus;
import fr.nordlys.shoploc.merchants.domain.service.ShopService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/** Socle M2: GET /api/v1/shops/mine. */
@WebMvcTest(ShopController.class)
class ShopMineControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShopService shopService;

    @Test
    void gives_the_shop_of_the_logged_in_merchant() throws Exception {
        when(shopService.findMine(10L, "MERCHANT")).thenReturn(new Shop(1L, 10L, "Le Fournil",
                "12 rue Solferino, Lille", "Boulangerie", null, ShopStatus.APPROVED, LocalDateTime.now()));

        mockMvc.perform(get("/api/v1/shops/mine").header("X-User-Id", "10").header("X-User-Role", "MERCHANT"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Le Fournil"));
    }

    @Test
    void answers_403_to_a_user_who_is_not_a_merchant() throws Exception {
        when(shopService.findMine(7L, "CLIENT"))
                .thenThrow(new OperationNotAllowedException("Only a merchant can manage a shop"));

        mockMvc.perform(get("/api/v1/shops/mine").header("X-User-Id", "7").header("X-User-Role", "CLIENT"))
                .andExpect(status().isForbidden());
    }

    @Test
    void answers_404_when_the_merchant_has_no_shop() throws Exception {
        when(shopService.findMine(10L, "MERCHANT")).thenThrow(new MerchantHasNoShopException(10L));

        mockMvc.perform(get("/api/v1/shops/mine").header("X-User-Id", "10").header("X-User-Role", "MERCHANT"))
                .andExpect(status().isNotFound());
    }
}
