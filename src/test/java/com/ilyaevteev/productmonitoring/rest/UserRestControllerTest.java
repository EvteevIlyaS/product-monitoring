package com.ilyaevteev.productmonitoring.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ilyaevteev.productmonitoring.dto.auth.NewEmailDto;
import com.ilyaevteev.productmonitoring.dto.auth.NewPasswordDto;
import com.ilyaevteev.productmonitoring.model.Category;
import com.ilyaevteev.productmonitoring.model.Product;
import com.ilyaevteev.productmonitoring.model.Store;
import com.ilyaevteev.productmonitoring.model.StoreProductPrice;
import com.ilyaevteev.productmonitoring.service.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class UserRestControllerTest {
    private final String END_POINT_PATH = "/api/v1/user/";
    private final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
    private final ObjectWriter ow;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private ProductService productService;

    @MockBean
    private StoreService storeService;

    @MockBean
    private CategoryService categoryService;

    @MockBean
    private StoreProductPriceService storeProductPriceService;

    UserRestControllerTest() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        this.ow = mapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    void changeEmailTest_checkAuth() throws Exception {
        mockMvc.perform(put(END_POINT_PATH + "email")
                        .queryParam("new-email", "user@gmail.com"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void changeEmailTest() throws Exception {
        NewEmailDto newPasswordDto = new NewEmailDto();
        String requestJson = ow.writeValueAsString(newPasswordDto);

        mockMvc.perform(put(END_POINT_PATH + "email")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void changePasswordTest() throws Exception {
        NewPasswordDto newPasswordDto = new NewPasswordDto();
        String requestJson = ow.writeValueAsString(newPasswordDto);

        mockMvc.perform(put(END_POINT_PATH + "password")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getStoresDirectoryTest() throws Exception {
        List<Store> stores = new ArrayList<>();
        Store store = new Store();
        stores.add(store);
        when(storeService.getStoresDirectory(anyInt(), anyInt())).thenReturn(stores);

        mockMvc.perform(get(END_POINT_PATH + "stores")
                        .queryParam("offset", "0")
                        .queryParam("page-size", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getCategoriesDirectoryTest() throws Exception {
        List<Category> categories = new ArrayList<>();
        Category category = new Category();
        categories.add(category);
        when(categoryService.getCategoriesDirectory(anyInt(), anyInt())).thenReturn(categories);

        mockMvc.perform(get(END_POINT_PATH + "categories")
                        .queryParam("offset", "0")
                        .queryParam("page-size", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getProductsByCategoryTest() throws Exception {
        List<Product> products = new ArrayList<>();
        Product product = new Product();
        products.add(product);
        when(productService.getProductsByCategory(anyString())).thenReturn(products);

        mockMvc.perform(get(END_POINT_PATH + "products")
                        .queryParam("category", "fruits"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getPricesDynamicsTest() throws Exception {
        List<StoreProductPrice> storeProductPrices = new ArrayList<>();
        StoreProductPrice storeProductPrice = new StoreProductPrice();
        storeProductPrices.add(storeProductPrice);
        when(storeProductPriceService.getProductPricesForPeriod(anyLong(), any(), any())).thenReturn(storeProductPrices);

        mockMvc.perform(get(END_POINT_PATH + "store-product-prices/1")
                        .queryParam("date-start", "2023-01-01")
                        .queryParam("date-end", "2023-01-02"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getStoreProductPricesComparisonTest() throws Exception {
        Long firstStoreId = 1L;
        Long secondStoreId = 2L;
        Map<Long, Long> storeIdPrices = new HashMap<>();
        storeIdPrices.put(firstStoreId, 100L);
        storeIdPrices.put(secondStoreId, 200L);
        Store mall = new Store();
        Store supermarket = new Store();
        mall.setName("mall");
        supermarket.setName("supermarket");
        when(storeProductPriceService.getCurrentStoreProductPrices(anyLong(), anyLong(), anyLong())).thenReturn(storeIdPrices);
        when(storeService.getStoreById(firstStoreId)).thenReturn(mall);
        when(storeService.getStoreById(secondStoreId)).thenReturn(supermarket);

        mockMvc.perform(get(END_POINT_PATH + "store-product-prices/comparison/1")
                        .queryParam("first-store-id", "1")
                        .queryParam("second-store-id", "2"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void getStoreProductPricesComparisonAllStoresTest() throws Exception {
        Long firstStoreId = 1L;
        List<Long> storeIds = new ArrayList<>();
        storeIds.add(firstStoreId);
        Store mall = new Store();
        mall.setName("mall");
        Map<Long, Long> allStoresProductPrices = new HashMap<>();
        allStoresProductPrices.put(firstStoreId, 100L);
        when(storeService.getAllStoreIds()).thenReturn(storeIds);
        when(storeProductPriceService.getAllStoresProductPrices(anyList(), anyLong())).thenReturn(allStoresProductPrices);
        when(storeService.getStoreById(firstStoreId)).thenReturn(mall);

        mockMvc.perform(get(END_POINT_PATH + "store-product-prices/comparison-all/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void provideProductPricesTest() throws Exception {
        mockMvc.perform(get(END_POINT_PATH + "store-product-prices/dynamics/1")
                        .queryParam("store-id", "1")
                        .queryParam("offset", "0")
                        .queryParam("page-size", "1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    void provideProductPricesOneStoreTest() throws Exception {
        mockMvc.perform(get(END_POINT_PATH + "store-product-prices/dynamics-all/1")
                        .queryParam("offset", "0")
                        .queryParam("page-size", "1"))
                .andExpect(status().isOk());
    }
}