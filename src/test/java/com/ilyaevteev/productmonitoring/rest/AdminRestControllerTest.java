package com.ilyaevteev.productmonitoring.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ilyaevteev.productmonitoring.dto.request.ProductRequestDto;
import com.ilyaevteev.productmonitoring.dto.response.ProductResponseDto;
import com.ilyaevteev.productmonitoring.dto.response.StoreDto;
import com.ilyaevteev.productmonitoring.dto.request.StoreProductPriceRequestDto;
import com.ilyaevteev.productmonitoring.dto.request.RegistrationRequestDto;
import com.ilyaevteev.productmonitoring.service.ProductService;
import com.ilyaevteev.productmonitoring.service.StoreProductPriceService;
import com.ilyaevteev.productmonitoring.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.Mockito.*;

@SpringBootTest
@AutoConfigureMockMvc
class AdminRestControllerTest {
    private final String END_POINT_PATH = "/api/v1/admin/";
    private final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
    private final ObjectWriter ow;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService;

    @MockBean
    private StoreProductPriceService storeProductPriceService;

    @MockBean
    private UserService userService;

    AdminRestControllerTest() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        this.ow = mapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    void addAdminTest_checkAuth() throws Exception {
        RegistrationRequestDto registrationRequestDto = new RegistrationRequestDto();
        String requestJson = ow.writeValueAsString(registrationRequestDto);
        when(userService.register(any(), any(), anyString(), any())).thenReturn(new HashMap<>());

        mockMvc.perform(post(END_POINT_PATH + "add-admin")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addAdminTest() throws Exception {
        RegistrationRequestDto registrationRequestDto = new RegistrationRequestDto();
        String requestJson = ow.writeValueAsString(registrationRequestDto);
        when(userService.register(any(), any(), anyString(), any())).thenReturn(new HashMap<>());

        mockMvc.perform(post(END_POINT_PATH + "add-admin")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProductTest() throws Exception {
        ProductRequestDto productRequestDto = new ProductRequestDto();
        String requestJson = ow.writeValueAsString(productRequestDto);

        mockMvc.perform(post(END_POINT_PATH + "products")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProductTest() throws Exception {
        ProductResponseDto productResponseDto = new ProductResponseDto();
        String requestJson = ow.writeValueAsString(productResponseDto);

        mockMvc.perform(put(END_POINT_PATH + "products")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteProductProductTest() throws Exception {
        mockMvc.perform(delete(END_POINT_PATH + "products/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createStoreProductPriceTest() throws Exception {
        StoreProductPriceRequestDto storeProductPriceRequestDto = new StoreProductPriceRequestDto();
        storeProductPriceRequestDto.setStore(new StoreDto());
        storeProductPriceRequestDto.setProduct(new ProductResponseDto());
        String requestJson = ow.writeValueAsString(storeProductPriceRequestDto);

        mockMvc.perform(post(END_POINT_PATH + "store-product-prices")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteStoreProductPriceTest() throws Exception {
        mockMvc.perform(delete(END_POINT_PATH + "store-product-prices/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void uploadProductsTest() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "products-data.csv",
                "text/csv",
                new ClassPathResource("upload-data/products-data.csv").getInputStream());

        mockMvc.perform(MockMvcRequestBuilders.multipart(END_POINT_PATH + "products/upload")
                        .file(multipartFile).characterEncoding("UTF-8"))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void uploadPricesTest() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "products-data.csv",
                "text/csv",
                new ClassPathResource("upload-data/prices-data.csv").getInputStream());

        mockMvc.perform(MockMvcRequestBuilders.multipart(END_POINT_PATH + "store-product-prices/upload")
                        .file(multipartFile).characterEncoding("UTF-8"))
                .andExpect(status().isCreated());
    }
}