package com.ilyaevteev.productmonitoring.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ilyaevteev.productmonitoring.dto.NoIdProductDto;
import com.ilyaevteev.productmonitoring.dto.ProductDto;
import com.ilyaevteev.productmonitoring.dto.StoreDto;
import com.ilyaevteev.productmonitoring.dto.StoreProductPriceDto;
import com.ilyaevteev.productmonitoring.dto.auth.RegistrationDto;
import com.ilyaevteev.productmonitoring.model.auth.User;
import com.ilyaevteev.productmonitoring.service.ProductService;
import com.ilyaevteev.productmonitoring.service.StoreProductPriceService;
import com.ilyaevteev.productmonitoring.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.nio.charset.StandardCharsets;

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
        RegistrationDto registrationDto = new RegistrationDto();
        String requestJson = ow.writeValueAsString(registrationDto);
        when(userService.register(any(), any(), anyString(), any())).thenReturn(new User());

        mockMvc.perform(post(END_POINT_PATH + "add-admin")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void addAdminTest() throws Exception {
        RegistrationDto registrationDto = new RegistrationDto();
        String requestJson = ow.writeValueAsString(registrationDto);
        when(userService.register(any(), any(), anyString(), any())).thenReturn(new User());

        mockMvc.perform(post(END_POINT_PATH + "add-admin")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createProductTest() throws Exception {
        NoIdProductDto noIdProductDto = new NoIdProductDto();
        String requestJson = ow.writeValueAsString(noIdProductDto);

        mockMvc.perform(post(END_POINT_PATH + "products")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateProductTest() throws Exception {
        ProductDto productDto = new ProductDto();
        String requestJson = ow.writeValueAsString(productDto);

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
        StoreProductPriceDto storeProductPriceDto = new StoreProductPriceDto();
        storeProductPriceDto.setStore(new StoreDto());
        storeProductPriceDto.setProduct(new ProductDto());
        String requestJson = ow.writeValueAsString(storeProductPriceDto);

        mockMvc.perform(post(END_POINT_PATH + "store-product-prices")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void uploadProductsTest() throws Exception {
        String dataLine = "name,categoryId\nbutter,3\npear,1\npumpkin,2";
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "products-data.csv",
                "text/csv",
                dataLine.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(END_POINT_PATH + "products/upload")
                        .file(multipartFile).characterEncoding("UTF-8"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void uploadPricesTest() throws Exception {
        String dataLine = "storeId,productId,price\n1,3,100\n2,2,200\n3,1,300";
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "products-data.csv",
                "text/csv",
                dataLine.getBytes());

        mockMvc.perform(MockMvcRequestBuilders.multipart(END_POINT_PATH + "store-product-prices/upload")
                        .file(multipartFile).characterEncoding("UTF-8"))
                .andExpect(status().isOk());
    }
}