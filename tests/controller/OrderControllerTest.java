package controller;

import database.DataAccessException;
import model.Order;
import model.*;
import org.junit.jupiter.api.*;

import java.util.List;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class OrderControllerTest {

    OrderController orderController;

    @BeforeEach
    void setUpData() {


        Address addressStore = new Address("999", "To the left", "long street",
                "Bratislava", "99999", "BA", "Slovakia");
        Store store = new Store("Bratislava1", "999", "bratislava@tevos.sk", addressStore);
        Address addressWarehouse = new Address("111", "no", "short street",
                "Prievidza", "11111", "PD", "Slovakia");
        Warehouse warehouse = new Warehouse("Warehouse1", "111", "warehouse1@tevos.sk", addressWarehouse);

        // act
        try {
            orderController = new OrderController();
            orderController.createOrder(store, warehouse);

            Product p = new Product("shampoo", 200, 5.99);
            int amount = 5;

            orderController.addProduct(p, amount);
        } catch (ControlException | DataAccessException e) {
            Assertions.fail("Exception thrown: " + e.getMessage());
        }
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void testCreateOrder() {
        // assert
        Order order = orderController.getOrder();
        Assertions.assertNotNull(order);
        Assertions.assertEquals("Warehouse1" , order.getWarehouse().getName(), "Expects the order to be from Warehouse1 warehouse");
        Assertions.assertEquals("Bratislava1" , order.getStore().getName(), "Expects the order to be from Bratislava1 store ");
    }
    
    @Test
    @org.junit.jupiter.api.Order(2)
    void testAddProduct() {
        // assert
        List<OrderItem> orderItems = orderController.getOrder().getItems();
        OrderItem orderItem = orderController.getOrder().getItems().get(0);
        Product product = orderItem.getProduct();

        Assertions.assertEquals(1 , orderItems.size(), "Expects 1 orderItem to be present");
        Assertions.assertEquals("shampoo" , product.getName(), "Expects the product to be a shampoo");
        Assertions.assertEquals(5 , orderItem.getQuantity(), "Expects the products quantity to be 5");
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testRemoveProductWithAmount() {
        // arrange
        OrderItem orderItem = orderController.getOrder().getItems().get(0);
        Product product = orderItem.getProduct();
        int amount = 2;

        // act
        orderController.removeProduct(product, amount);

        // assert
        List<OrderItem> orderItems = orderController.getOrder().getItems();
        orderItem = orderController.getOrder().getItems().get(0);

        Assertions.assertEquals(1, orderItems.size() , "Expects the order item to be still present after removing not the whole quantity");
        Assertions.assertEquals(3, orderItem.getQuantity() , "Expects the order items quantity to be 3");
    }

    @Test
    @org.junit.jupiter.api.Order(4)
    void testRemoveProduct() {
        // arrange
        Product product = orderController.getOrder().getItems().get(0).getProduct();
        // act
        orderController.removeProduct(product);

        // assert
        List<OrderItem> orderItems = orderController.getOrder().getItems();

        Assertions.assertEquals(0, orderItems.size() , "Expects no order items to be present after complete removal");
    }
}
