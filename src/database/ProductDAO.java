package database;

import model.Product;
import model.Warehouse;

import java.util.List;

/**
 * This DAO interface is used by the ProductController
 *
 * @see controller.ProductController
 */
public interface ProductDAO extends DAOInterface<Product> {

    List<Product> getProducts(Warehouse warehouse) throws DataAccessException;

    boolean checkAvailability(Warehouse warehouse, Product product, int amount) throws DataAccessException;

}
