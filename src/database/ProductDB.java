package database;

import model.Product;
import model.Warehouse;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO class for Product via DAOInterface and ProductDAO
 *
 * @author dmai0919-group3@UCNDK.onmicrosoft.com
 */
public class ProductDB implements ProductDAO {
    DBConnection db = DBConnection.getInstance();

    /**
     * Default constructor so we can pass along the DataAccessException from the DBConnection
     *
     * @throws DataAccessException when DBConnection throws a DataAccessException is thrown forward
     */
    public ProductDB() throws DataAccessException {
        //This constructor is empty because it only exists to pass along DataAccessException from DBConnection.getInstance()
    }

    /**
     * Creates a new product and inserts it into the DB
     *
     * @param value it's the given Product object
     * @return the generated ID key of the product
     * @throws DataAccessException
     */
    @Override
    public int create(Product value) throws DataAccessException {
        String queryProd = "INSERT INTO Product (name, weight, price) VALUES (?, ?, ?);";
        int productID = -1;
        try (PreparedStatement s = db.getDBConn().prepareStatement(queryProd, Statement.RETURN_GENERATED_KEYS)) {
            s.setString(1, value.getName()); // name
            s.setDouble(2, value.getWeight()); // weight
            s.setDouble(3, value.getPrice()); // price
            productID = db.executeInsertWithID(s);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return productID;
    }

    /**
     * @inheritDoc
     * @param id is the ID which we want to search for in the database
     * @return the single object with the given ID
     * @see DBConnection executeSelect() method
     */
    @Override
    public Product selectByID(int id) throws DataAccessException {
        String query = "SELECT TOP 1 * FROM Product WHERE id=?;";
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, id);
            ResultSet rs = db.executeSelect(s);
            if (rs.next()) {
                if (rs.getString("name") != null) {
                    return new Product(
                            rs.getInt("id"),
                            rs.getString("name"),
                            rs.getDouble("weight"),
                            rs.getDouble("price"));
                } else return null;
            } else return null;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * @inheritDoc
     * @return List of all entities
     * @throws DataAccessException
     */
    @Override
    public List<Product> all() throws DataAccessException {
        String query = "SELECT * FROM Product;";
        try {
            PreparedStatement s = db.getDBConn().prepareStatement(query);
            ResultSet rs = db.executeSelect(s);
            List<Product> resultList = new ArrayList<>();

            while (rs.next()) {
                resultList.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("weight"),
                        rs.getDouble("price")));
            }
            return resultList;

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * @inheritDoc
     * @param value it's the given T type object (in this case Product)
     * @return the number of rows affected by the update
     * @see DBConnection executeQuery() method
     */
    @Override
    public int update(Product value) throws DataAccessException {
        String queryProduct = "UPDATE Product SET name=?, weight=?, price=? WHERE id=?;";
        try (PreparedStatement s = db.getDBConn().prepareStatement(queryProduct)) {
            s.setString(1, value.getName());
            s.setDouble(2, value.getWeight());
            s.setDouble(3, value.getPrice());
            s.setInt(4, value.getId());
            return db.executeQuery(s);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * @inheritDoc
     * @param value it's the given T type object (in this case Product)
     * @return the number of rows deleted from the table (1 or 0)
     * @see DBConnection executeQuery()
     */
    @Override
    public int delete(Product value) throws DataAccessException {
        // Because of the tables cascade rule, we dont need to have separate stock query
        String query = "DELETE Product WHERE id=?";
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, value.getId());
            return db.executeQuery(s);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * @inheritDoc
     * @param warehouse
     * @return the list of products of the warehouse
     * @see DBConnection executeSelect() method
     */
    public List<Product> getProducts(Warehouse warehouse) throws DataAccessException {
        String query = "SELECT * FROM Product p " +
                "JOIN Stock s ON p.id = s.productID " +
                "JOIN Warehouse w ON s.warehouseID = w.id " +
                "WHERE w.id = ?;";

        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, warehouse.getId());
            ResultSet rs = db.executeSelect(s);
            List<Product> resultList = new ArrayList<>();

            while (rs.next()) {
                resultList.add(new Product(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getDouble("weight"),
                        rs.getDouble("price")));
            }
            return resultList;

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * @param warehouse Where you want to check the availability of a product
     * @param product   Product to check
     * @param amount    Amount of product needed
     * @return if the stock of the warehouse check the amount of product
     * @inheritDoc
     * @see DBConnection executeSelect() method
     */
    @Override
    public boolean checkAvailability(Warehouse warehouse, Product product, int amount) throws DataAccessException {
        String query = "SELECT TOP 1 * FROM Product p " +
                "JOIN Stock s ON p.id = s.productID " +
                "JOIN Warehouse w ON s.warehouseID = w.id " +
                "WHERE w.id = ? AND ? <= s.quantity;";

        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, warehouse.getId());
            s.setInt(2, amount);
            ResultSet rs = db.executeSelect(s);

            return (rs.next());
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
