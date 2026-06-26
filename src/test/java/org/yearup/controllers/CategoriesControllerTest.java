package org.yearup.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.yearup.service.CategoryService;
import org.yearup.service.ProductService;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoriesController.class)
class CategoriesControllerTest
{
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CategoryService categoryService;

    @MockitoBean
    private ProductService productService;   // required: controller's constructor needs it too

    @Test

    void getById_whenCategoryMissing_returns404() throws Exception
    {
        // arrange: service says "no such category"
        when(categoryService.getById(9999)).thenReturn(null);

        // act + assert: the endpoint answers 404
        mockMvc.perform(get("/categories/9999"))
                .andExpect(status().isNotFound());
    }
}