package com.ilyaevteev.productmonitoring.rest.user;

import com.ilyaevteev.productmonitoring.model.Category;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CategoryRestControllerTest {
    private final String END_POINT_PATH = "/api/v1/user/categories";

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CategoryService categoryService;

    @Test
    @WithMockUser(roles = "USER")
    void getCategoriesDirectoryTest() throws Exception {
        List<Category> categories = List.of(new Category());
        Page<Category> page = new PageImpl<>(categories);
        when(categoryService.getCategoriesDirectory(any())).thenReturn(page);

        mockMvc.perform(get(END_POINT_PATH)
                        .queryParam("page", "0")
                        .queryParam("size", "1"))
                .andExpect(status().isOk());
    }
}