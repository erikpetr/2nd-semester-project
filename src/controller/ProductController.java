package controller;

import database.DataAccessException;
import database.ProductDAO;
import database.ProductDB;
import model.Product;
import model.Warehouse;

import java.util.List;

/**
 * Controller class which connects the GUI with the DAO
 *
 * @author dmai0919-group3@UCNDK.onmicrosoft.com
 */
public class ProductController {
    private final ProductDAO productDAO;

    /**
     * Simple constructor which initializes the ProductDB for this controller
     *
     * @throws DataAccessException
     */
    public ProductController() throws DataAccessException {
        productDAO = new ProductDB();
    }

    /**
     * Creates a new product with given details and then writes it into the database
     *
     * @param product the Product object we want to insert into the DB
     * @return the Product object inserted into the DB (with the correct ID)
     * @throws DataAccessException
     */
    public Product createProduct(Product product) throws DataAccessException {
        int id = productDAO.create(product);
        return productDAO.selectByID(id);
    }

    /**
     * Deletes a Product from the database
     *
     * @param product the product object to be deleted
     * @throws DataAccessException
     */
    public int deleteProduct(Product product) throws DataAccessException {
        return productDAO.delete(product);
    }

    /**
     * Finds all Products in a given Warehouse
     *
     * @param warehouse the warehouse we're searching for
     * @return the List of Products in the Warehouse
     * @throws DataAccessException
     */
    public List<Product> getProducts(Warehouse warehouse) throws DataAccessException {
        return productDAO.getProducts(warehouse);
    }

    /**
     * Finds a product with a given ID in the DB
     *
     * @param id the ID we're searching for
     * @return the Product object or null if not found
     * @throws DataAccessException
     */
    public Product getProductByID(int id) throws DataAccessException {
        return productDAO.selectByID(id);
    }

    /**
     * Finds all products from the DB
     *
     * @return a List containing every product
     * @throws DataAccessException
     */
    public List<Product> all() throws DataAccessException {
        return productDAO.all();
    }

    /**
     * Checks if a given amount of a given Product is available in a Warehouse
     *
     * @param warehouse the Warehouse we're searching for
     * @param product   the Product we're searching
     * @param amount    the amount of Products
     * @return true if it's available, false if not
     * @throws DataAccessException
     */
    public Boolean checkAvailability(Warehouse warehouse, Product product, int amount) throws DataAccessException {
        return productDAO.checkAvailability(warehouse, product, amount);
    }

    public int updateProduct(Product p) throws DataAccessException {
        return productDAO.update(p);
    }
}
