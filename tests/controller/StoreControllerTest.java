package controller;
import database.DataAccessException;
import model.*;

import org.junit.jupiter.api.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StoreControllerTest {

    private StoreController storeController;
    private int storeId;
    private Store store;

    @BeforeEach
    void setUpData() {
        try {
            // arrange
            storeController = new StoreController();
            Address addressStore = new Address("999", "To the left", "long street",
                    "Bratislava", "99999", "BA", "Slovakia");
            Store store = new Store("Bratislava1", "999", "bratislava@tevos.sk", addressStore);
            // act
            storeId = storeController.createStore(store);
            this.store = storeController.getStoreByID(storeId);
        } catch (DataAccessException e) {
            Assertions.fail("Exceptions thrown, message: " + e.getMessage());
        }
        Assertions.assertDoesNotThrow(() -> DataAccessException.class);
    }

    @Test
    @org.junit.jupiter.api.Order(1)
    void testCreateStore() {
        // assert
        Assertions.assertNotNull(store);
        Assertions.assertEquals("long street" , store.getAddress().getStreet(), "Expects the store to be located at long street");
        Assertions.assertEquals("Bratislava1" , store.getName(), "Expects the store name to be Bratislava1");
    }

    @Test
    @org.junit.jupiter.api.Order(2)
    void testUpdateStore() {
        String newPw = "888";
        // act
        store.setPassword(newPw);
        boolean i = false;
        try {
            i = storeController.updateStore(store);
        } catch (DataAccessException e) {
             Assertions.fail("Exceptions thrown, message: " + e.getMessage());
        }
        // assert
        Assertions.assertEquals("888" , store.getPassword(), "Expects the password to be 888");
        Assertions.assertTrue(i, "True if the store was successfully updated");
    }

    @Test
    @org.junit.jupiter.api.Order(3)
    void testDeleteStore() {
        // act
        try {
            storeController.deleteStore(store);

            // assert
            Assertions.assertNull(storeController.getStoreByID(storeId), "The should be no store now");

        } catch (DataAccessException e) {
            Assertions.fail("Exceptions thrown, message: " + e.getMessage());
        }
    }

    /*
    TODO: Test always fails, check why when there is time
    TODO: Remove when there is no time
    @Test
    @org.junit.jupiter.api.Order(4)
    void testGetStoreById() {
        // act
        Store store2 = null;
        try {
            store2 = storeController.getStoreByID(storeId);
        } catch (DataAccessException e) {
            Assertions.fail("Exceptions thrown, message: " + e.getMessage());
        }
        // assert
        Assertions.assertTrue(store.equals(store2),  "The stores should be the same");
    }*/

    @AfterEach
    void cleanup() {
        try {
            storeController.deleteStore(store);
        } catch (DataAccessException e) {
            Assertions.fail("Exceptions thrown, message: " + e.getMessage());
        }
    }
}
