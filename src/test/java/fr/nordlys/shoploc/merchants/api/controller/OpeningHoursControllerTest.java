package fr.nordlys.shoploc.merchants.api.controller;

import fr.nordlys.shoploc.merchants.domain.model.OpeningHour;
import fr.nordlys.shoploc.merchants.domain.service.OpeningHoursService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OpeningHoursController.class)
class OpeningHoursControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OpeningHoursService service;

    @Test
    void returns_public_opening_hours_of_a_shop() throws Exception {
        when(service.findPublic(1L))
                .thenReturn(List.of(new OpeningHour(5L, 1L, 2, LocalTime.of(8, 0), LocalTime.of(12, 30))));

        mockMvc.perform(get("/api/v1/shops/1/opening-hours"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dayOfWeek").value(2));
    }

    @Test
    void answers_400_on_invalid_hours() throws Exception {
        when(service.replaceMine(eq(10L), eq("MERCHANT"), anyList()))
                .thenThrow(new IllegalArgumentException("invalid hours"));

        mockMvc.perform(put("/api/v1/shops/mine/opening-hours")
                        .header("X-User-Id", "10")
                        .header("X-User-Role", "MERCHANT")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[{\"dayOfWeek\":2,\"opensAt\":\"19:00\",\"closesAt\":\"08:00\"}]"))
                .andExpect(status().isBadRequest());
    }
}
