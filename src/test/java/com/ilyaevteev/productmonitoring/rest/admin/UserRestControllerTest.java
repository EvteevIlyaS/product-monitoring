package com.ilyaevteev.productmonitoring.rest.admin;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.ilyaevteev.productmonitoring.dto.request.RegistrationRequestDto;
import com.ilyaevteev.productmonitoring.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserRestControllerTest {
    private final String END_POINT_PATH = "/api/v1/admin";
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
    void addAdminTest_checkAuth() throws Exception {
        RegistrationRequestDto registrationRequestDto = new RegistrationRequestDto();
        String requestJson = ow.writeValueAsString(registrationRequestDto);
        when(userService.register(any(), any(), anyString(), any())).thenReturn(new HashMap<>());

        mockMvc.perform(post(END_POINT_PATH)
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

        mockMvc.perform(post(END_POINT_PATH)
                        .contentType(APPLICATION_JSON_UTF8)
                        .content(requestJson))
                .andExpect(status().isCreated());
    }
}