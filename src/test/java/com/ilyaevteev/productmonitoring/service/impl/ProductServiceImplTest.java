package com.ilyaevteev.productmonitoring.service.impl;

import com.ilyaevteev.productmonitoring.model.Product;
import com.ilyaevteev.productmonitoring.repository.ProductRepository;
import com.ilyaevteev.productmonitoring.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mock.web.MockMultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {
    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryService categoryService;

    @InjectMocks
    private ProductServiceImpl productService;

    @Test
    void getProductsByCategory_checkReturnedValue() {
        String category = "fruits";
        List<Product> products = List.of(new Product());
        when(productRepository.getProductsByCategoryName(category)).thenReturn(products);

        List<Product> productsRes = productService.getProductsByCategory(category);

        assertThat(productsRes).isEqualTo(products);
    }

    @Test
    void addProduct_checkMethodInvocation() {
        Product product = new Product();

        productService.addProduct(product);

        verify(productRepository, times(1)).save(product);
    }

    @Test
    void addProduct_checkThrowException() {
        Product product = new Product();
        when(productRepository.save(product)).thenThrow(new RuntimeException());

        assertThatThrownBy(() -> productService.addProduct(product))
                .hasMessage("Wrong product data");
    }

    @Test
    void updateProduct_checkMethodInvocation() {
        Product product = new Product();
        product.setId(1L);
        when(productRepository.findById(any())).thenReturn(Optional.of(new Product()));

        productService.updateProduct(product);

        verify(productRepository, times(1)).updateProductNameAndCategory(product.getName(),
                product.getCategory(), product.getId());
    }

    @Test
    void updateProduct_checkFirstThrowException() {
        Product product = new Product();

        assertThatThrownBy(() -> productService.updateProduct(product))
                .hasMessage("Wrong product data");
    }

    @Test
    void updateProduct_checkSecondThrowException() {
        Product product = new Product();
        product.setId(1L);
        when(productRepository.findById(any())).thenThrow(new RuntimeException("No products found by id"));

        assertThatThrownBy(() -> productService.updateProduct(product))
                .hasMessage("Wrong product data");
    }

    @Test
    void updateProduct_checkThirdThrowException() {
        Product product = new Product();
        product.setId(1L);
        when(productRepository.findById(any())).thenReturn(Optional.of(new Product()));
        doThrow(new RuntimeException("")).when(productRepository).updateProductNameAndCategory(any(), any(), any());

        assertThatThrownBy(() -> productService.updateProduct(product))
                .hasMessage("Wrong product data");
    }

    @Test
    void deleteProduct_checkMethodInvocation() {
        Long id = 1L;

        productService.deleteProduct(id);

        verify(productRepository, times(1)).deleteById(id);
    }

    @Test
    void getProductById_checkReturnedValue() {
        Long id = 1L;
        Product product = new Product();
        Optional<Product> optionalProduct = Optional.of(product);
        when(productRepository.findById(id)).thenReturn(optionalProduct);

        Product productRes = productService.getProductById(id);

        assertThat(productRes).isEqualTo(product);
    }

    @Test
    void getProductById_checkThrowException() {
        Long id = 1L;
        Optional<Product> optionalProduct = Optional.empty();
        when(productRepository.findById(id)).thenReturn(optionalProduct);

        assertThatThrownBy(() -> productService.getProductById(id))
                .hasMessage("No products found by id: " + id);
    }

    @Test
    void uploadFileProduct_checkMethodInvocationCSV() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "products-data.csv",
                "text/csv",
                new ClassPathResource("upload-data/products-data.csv").getInputStream());

        productService.uploadFileProduct(multipartFile);

        verify(productRepository, times(1)).saveAll(anyIterable());
    }

    @Test
    void uploadFileProduct_checkMethodInvocationXLSX() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "products-data.xlsx",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                new ClassPathResource("upload-data/products-data.xlsx").getInputStream());

        productService.uploadFileProduct(multipartFile);

        verify(productRepository, times(1)).saveAll(anyIterable());
    }

    @Test
    void uploadFileProduct_checkFirstThrowException() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "products-data.csv",
                "text/csv",
                new ClassPathResource("upload-data/products-data.csv").getInputStream());
        when(productRepository.saveAll(anyIterable())).thenThrow(new RuntimeException(""));

        assertThatThrownBy(() -> productService.uploadFileProduct(multipartFile))
                .hasMessage("Fail to store data");
    }

    @Test
    void uploadFileProduct_checkSecondThrowException() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "products-data.csv",
                "odt",
                new ClassPathResource("upload-data/products-data.csv").getInputStream());

        assertThatThrownBy(() -> productService.uploadFileProduct(multipartFile))
                .hasMessage("Wrong data format");
    }

    @Test
    void uploadFileProduct_checkThirdThrowException() throws IOException {
        MockMultipartFile multipartFile = new MockMultipartFile("file",
                "products-data.csv",
                "text/csv",
                new ClassPathResource("upload-data/products-data.csv").getInputStream());
        when(categoryService.getCategoryById(anyLong())).thenThrow(new RuntimeException("No categories found by id"));

        assertThatThrownBy(() -> productService.uploadFileProduct(multipartFile))
                .hasMessage("Fail to store data");
    }
}