package com.ilyaevteev.productmonitoring.rest.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ilyaevteev.productmonitoring.dto.request.NewEmailDto;
import com.ilyaevteev.productmonitoring.dto.request.NewPasswordDto;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserRestControllerTest {
    private final String END_POINT_PATH = "/api/v1/user";
    private final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(), StandardCharsets.UTF_8);
    private final ObjectWriter ow;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    UserRestControllerTest() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.WRAP_ROOT_VALUE, false);
        this.ow = mapper.writer().withDefaultPrettyPrinter();
    }

    @Test
    void changeEmailTest_checkAuth() throws Exception {
        mockMvc.perform(put(END_POINT_PATH + "/email")
                        .queryParam("new-email", "user@gmail.com"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(roles = "USER")
    void changeEmailTest() throws Exception {
        NewEmailDto newPasswordDto = new NewEmailDto();
        String requestJson = ow.writeValueAsString(newPasswordDto);

        mockMvc.perform(put(END_POINT_PATH + "/email")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(roles = "USER")
    void changePasswordTest() throws Exception {
        NewPasswordDto newPasswordDto = new NewPasswordDto();
        String requestJson = ow.writeValueAsString(newPasswordDto);

        mockMvc.perform(put(END_POINT_PATH + "/password")
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isNoContent());
    }
}