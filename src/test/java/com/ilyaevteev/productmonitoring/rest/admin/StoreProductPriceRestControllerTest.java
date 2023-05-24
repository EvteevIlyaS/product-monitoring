package com.ilyaevteev.productmonitoring.rest.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ilyaevteev.productmonitoring.dto.request.StoreProductPriceRequestDto;
import com.ilyaevteev.productmonitoring.service.StoreProductPriceService;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class StoreProductPriceRestControllerTest {
    private final String END_POINT_PATH = "/api/v1/admin/store-product-prices";
    private final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
    private final ObjectWriter ow;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StoreProductPriceService storeProductPriceService;

    StoreProductPriceRestControllerTest() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        this.ow = mapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createStoreProductPriceTest() throws Exception {
        StoreProductPriceRequestDto storeProductPriceRequestDto = new StoreProductPriceRequestDto();
        storeProductPriceRequestDto.setStoreId(1L);
        storeProductPriceRequestDto.setProductId(1L);
        String requestJson = ow.writeValueAsString(storeProductPriceRequestDto);

        mockMvc.perform(post(END_POINT_PATH)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteStoreProductPriceTest() throws Exception {
        mockMvc.perform(delete(END_POINT_PATH + "/1"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void uploadPricesTest() throws Exception {
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "products-data.csv",
                "text/csv",
                new ClassPathResource("upload-data/prices-data.csv").getInputStream());

        mockMvc.perform(MockMvcRequestBuilders.multipart(END_POINT_PATH + "/upload")
                        .file(multipartFile).characterEncoding("UTF-8"))
                .andExpect(status().isCreated());
    }
}