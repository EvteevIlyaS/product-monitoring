package com.ilyaevteev.productmonitoring.rest.user;

import com.ilyaevteev.productmonitoring.model.StoreProductPrice;
import com.ilyaevteev.productmonitoring.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StoreProductPriceRestControllerTest {
    private final String END_POINT_PATH = "/api/v1/user/store-product-prices";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreProductPriceService storeProductPriceService;

    @Test
    @WithMockUser(roles = "USER")
    void getPricesDynamicsTest() throws Exception {
        List<StoreProductPrice> storeProductPrices = List.of(new StoreProductPrice());
        Page<StoreProductPrice> page = new PageImpl<>(storeProductPrices);
        when(storeProductPriceService.getProductPricesForPeriod(anyLong(), any(), any(), any())).thenReturn(page);

        mockMvc.perform(get(END_POINT_PATH + "/1")
                        .queryParam("date-start", "2023-01-01")
                        .queryParam("date-end", "2023-01-02"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getStoreProductPricesComparisonTest() throws Exception {
        Map<String, Map<String, String>> storeIdPrices = Map.of("firstStore",
                Map.of("store", "mall",
                        "price", "100"),
                "secondStore",
                Map.of("store", "supermarket",
                        "price", "200")
        );
        when(storeProductPriceService.getCurrentStoreProductPrices(anyLong(), anyLong(), anyLong())).thenReturn(storeIdPrices);

        mockMvc.perform(get(END_POINT_PATH + "/comparison/1")
                        .queryParam("first-store-id", "1")
                        .queryParam("second-store-id", "2"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getStoreProductPricesComparisonAllStoresTest() throws Exception {
        List<Map<String, String>> allStoresProductPrices = Arrays.asList(
                Map.of("store", "mall",
                        "price", "100"),
                Map.of("store", "supermarket",
                        "price", "200")
        );
        Page<Map<String, String>> page = new PageImpl<>(allStoresProductPrices);
        when(storeProductPriceService.getAllStoresProductPrices(anyLong(), any())).thenReturn(page);

        mockMvc.perform(get(END_POINT_PATH + "/comparison-all/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void provideProductPricesTest() throws Exception {
        List<Map<String, String>> allStoresProductPrices = new ArrayList<>();
        Page<Map<String, String>> page = new PageImpl<>(allStoresProductPrices);
        when(storeProductPriceService.getProductPricesOneStore(anyLong(), anyLong(), any())).thenReturn(page);
        mockMvc.perform(get(END_POINT_PATH + "/dynamics/1")
                        .queryParam("store-id", "1")
                        .queryParam("page", "0")
                        .queryParam("size", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void provideProductPricesOneStoreTest() throws Exception {
        List<Map<String, String>> allStoresProductPrices = new ArrayList<>();
        Page<Map<String, String>> page = new PageImpl<>(allStoresProductPrices);
        when(storeProductPriceService.getProductPrices(anyLong(), any())).thenReturn(page);
        mockMvc.perform(get(END_POINT_PATH + "/dynamics-all/1")
                        .queryParam("page", "0")
                        .queryParam("size", "1"))
                .andExpect(status().isOk());
    }
}