package fr.nordlys.shoploc.merchants.api.controller;

import fr.nordlys.shoploc.merchants.domain.exception.OperationNotAllowedException;
import fr.nordlys.shoploc.merchants.domain.model.Shop;
import fr.nordlys.shoploc.merchants.domain.model.ShopStatus;
import fr.nordlys.shoploc.merchants.domain.service.ShopService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ShopController.class)
class ShopControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ShopService shopService;

    private static Shop shop(ShopStatus status) {
        return new Shop(1L, 10L, "Le Fournil", "12 rue Solferino, Lille", "Boulangerie",
                null, status, LocalDateTime.now());
    }

    @Test
    void registers_a_shop_and_answers_201() throws Exception {
        when(shopService.register(10L, "MERCHANT", "Le Fournil", "12 rue Solferino, Lille", "Boulangerie", null))
                .thenReturn(shop(ShopStatus.PENDING));

        mockMvc.perform(post("/api/v1/shops")
                        .header("X-User-Id", "10")
                        .header("X-User-Role", "MERCHANT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Le Fournil", "address": "12 rue Solferino, Lille", "category": "Boulangerie"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("PENDING"));
    }

    @Test
    void answers_400_when_the_name_is_missing() throws Exception {
        mockMvc.perform(post("/api/v1/shops")
                        .header("X-User-Id", "10")
                        .header("X-User-Role", "MERCHANT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"address": "12 rue Solferino, Lille", "category": "Boulangerie"}
                                """))
                .andExpect(status().isBadRequest());
        verify(shopService, never()).register(any(), any(), any(), any(), any(), any());
    }

    @Test
    void answers_403_when_the_user_is_not_a_merchant() throws Exception {
        when(shopService.register(10L, "CLIENT", "Le Fournil", "12 rue Solferino, Lille", "Boulangerie", null))
                .thenThrow(new OperationNotAllowedException("Only a merchant can register a shop"));

        mockMvc.perform(post("/api/v1/shops")
                        .header("X-User-Id", "10")
                        .header("X-User-Role", "CLIENT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name": "Le Fournil", "address": "12 rue Solferino, Lille", "category": "Boulangerie"}
                                """))
                .andExpect(status().isForbidden());
    }
}
