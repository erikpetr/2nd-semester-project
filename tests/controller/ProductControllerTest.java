package controller;

import database.DataAccessException;
import model.Product;
import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ProductControllerTest {

    ProductController productController;
    int id1 = -1;
    int id2 = -1;
    int id3 = -1;
    Product product1 = new Product("Test 1", 100, 500.00);
    Product product2 = new Product("Test 2", 500, 999999999.99);
    Product product3 = new Product("Test 3", -9999999, 0.01);

    @BeforeEach
    void setUpData() {
        try {
            productController = new ProductController();
        } catch (DataAccessException e) {
            Assertions.fail("DataAccessException thrown\n" + e.getMessage());
        }
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void testCreateProductAndGetProductByID() throws DataAccessException {
        try {
            //Act
            id1 = productController.createProduct(product1).getId();
            id2 = productController.createProduct(product2).getId();
            id3 = productController.createProduct(product3).getId();
        } catch (DataAccessException e) {
            e.printStackTrace();
            Assertions.fail("DataAccessException thrown\n" + e.getMessage());
        }

        //Assert product1
        Assertions.assertEquals(productController.getProductByID(id1).getName(), product1.getName(), "The name for productID " + id1 + " should be " + product1.getName());
        Assertions.assertEquals(productController.getProductByID(id1).getWeight(), product1.getWeight(), "The weight for productID " + id1 + " should be " + product1.getWeight());
        Assertions.assertEquals(productController.getProductByID(id1).getPrice(), product1.getPrice(), "The name for productID " + id1 + " should be " + product1.getPrice());
        //Assert product2
        Assertions.assertEquals(productController.getProductByID(id2).getName(), product2.getName(), "The name for productID " + id2 + " should be " + product2.getName());
        Assertions.assertEquals(productController.getProductByID(id2).getWeight(), product2.getWeight(), "The weight for productID " + id2 + " should be " + product2.getWeight());
        Assertions.assertEquals(productController.getProductByID(id2).getPrice(), product2.getPrice(), "The name for productID " + id2 + " should be " + product2.getPrice());
        //Assert product3
        Assertions.assertEquals(productController.getProductByID(id3).getName(), product3.getName(), "The name for productID " + id3 + " should be " + product3.getName());
        Assertions.assertEquals(productController.getProductByID(id3).getWeight(), product3.getWeight(), "The weight for productID " + id3 + " should be " + product3.getWeight());
        Assertions.assertEquals(productController.getProductByID(id3).getPrice(), product3.getPrice(), "The name for productID " + id3 + " should be " + product3.getPrice());
    }
}
