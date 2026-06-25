package org.yearup.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.yearup.models.Product;
import org.yearup.repository.ProductRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest
{
    @Mock
    private ProductRepository productRepository;   // fake repo — no database

    @InjectMocks
    private ProductService productService;         // real service, fake repo injected

    @Test
    public void search_withNoFilters_returnsAllProducts_includingNonFeatured()
    {
        // arrange: one featured product, one NOT featured
        Product featured = new Product(1, "Featured Tee", 19.99, 1,
                "a featured shirt", "Black", 10, true, "tee.jpg");
        Product notFeatured = new Product(2, "Plain Tee", 14.99, 1,
                "a plain shirt", "White", 10, false, "plain.jpg");

        when(productRepository.findAll()).thenReturn(List.of(featured, notFeatured));

        // act: no filters at all
        List<Product> result = productService.search(null, null, null, null);

        // assert: BOTH come back — this is the assertion that catches the bug
        assertEquals(2, result.size(),
                "Search with no filters should return every product, featured or not.");
        assertTrue(result.contains(notFeatured),
                "The non-featured product must not be dropped.");
    }

    @Test
    public void search_withPriceRange_returnsOnlyProductsInRange()
    {
        // arrange: three products at different price points
        Product cheap = new Product(1, "Cheap", 10.00, 1, "", "Black", 5, false, "a.jpg");
        Product mid   = new Product(2, "Mid",   50.00, 1, "", "Black", 5, false, "b.jpg");
        Product pricey= new Product(3, "Pricey",90.00, 1, "", "Black", 5, false, "c.jpg");

        when(productRepository.findAll()).thenReturn(List.of(cheap, mid, pricey));

        // act: only want 25–75
        List<Product> result = productService.search(null, 25.0, 75.0, null);

        // assert: only the mid-priced one qualifies
        assertEquals(1, result.size(), "Only products between 25 and 75 should match.");
        assertTrue(result.contains(mid));
    }
    @Test
    public void update_changesStock_andPersistsIt()
    {
        // arrange: the product currently in the "database" has stock 10
        Product existing = new Product(1, "Tee", 19.99, 1, "desc", "Black", 10, false, "tee.jpg");
        when(productRepository.findById(1)).thenReturn(java.util.Optional.of(existing));
        // save returns whatever it's given (the updated entity)
        when(productRepository.save(org.mockito.ArgumentMatchers.any(Product.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        // the incoming edit sets stock to 999
        Product incoming = new Product(0, "Tee", 19.99, 1, "desc", "Black", 999, false, "tee.jpg");

        // act
        Product result = productService.update(1, incoming);

        // assert: the new stock made it through — this fails before setStock is added
        assertEquals(999, result.getStock(), "Stock must persist on update.");
    }
}