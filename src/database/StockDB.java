package database;

import model.Stock;
import model.Product;
import model.Warehouse;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * DAO class for Stock via DAOInterface and StockDAO
 *
 * @author dmai0919-group3@UCNDK.onmicrosoft.com
 */
public class StockDB implements StockDAO {
    DBConnection db = DBConnection.getInstance();

    /**
     * Default constructor so we can pass along the DataAccessException from the DBConnection
     *
     * @throws DataAccessException when DBConnection throws a DataAccessException is thrown forward
     */
    public StockDB() throws DataAccessException {
        //This constructor is empty because it only exists to pass along DataAccessException from DBConnection.getInstance()
    }

    /**
     * Creates a new stock and inserts it into the DB
     *
     * @param value it's the given Stock object
     * @return the generated ID key of the stock
     * @throws DataAccessException
     */
    @Override
    public int create(Stock value) throws DataAccessException {

        String queryProd = "INSERT INTO Stock ('warehouseID', 'productID', 'quantity', 'minQuantity') VALUES (?, ?, ?, ?);";
        int stockID = -1;
        try (PreparedStatement s = db.getDBConn().prepareStatement(queryProd, Statement.RETURN_GENERATED_KEYS)) {
            s.setInt(1, value.getWarehouse().getId());
            s.setInt(2, value.getProduct().getId());
            s.setInt(3, value.getQuantity());
            s.setInt(4, value.getMinQuantity());
            stockID = db.executeInsertWithID(s);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return stockID;
    }

    /**
     * @inheritDoc
     * @param id is the ID which we want to search for in the database
     * @hidden This can't be implemented, do not use
     * @return null
     * @see DBConnection executeSelect() method
     */
    @Override
    public Stock selectByID(int id) throws DataAccessException {
        return null;
    }

    @Override
    public Stock getStock(int warehouseId, int productId) throws DataAccessException {
        String query = "SELECT TOP 1 * FROM Stock WHERE warehouseID=? AND productID =?;";
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, warehouseId);
            s.setInt(2, productId);
            ResultSet rs = db.executeSelect(s);
            if (rs.next()) {
                ProductDB productDB = new ProductDB();
                Product product = productDB.selectByID(rs.getInt("productID"));
                WarehouseDB warehouseDB = new WarehouseDB();
                Warehouse warehouse = warehouseDB.selectByID(rs.getInt("warehouseID"));
                return new Stock(
                        rs.getInt("quantity"),
                        rs.getInt("minQuantity"),
                        product,
                        warehouse);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    /**
     * @inheritDoc
     * @return List of all entities
     * @throws DataAccessException
     */
    @Override
    public List<Stock> all() throws DataAccessException {
        String query = "SELECT * FROM Stock;";
        try {
            PreparedStatement s = db.getDBConn().prepareStatement(query);
            ResultSet rs = db.executeSelect(s);
            List<Stock> resultList = new ArrayList<>();

            while (rs.next()) {ProductDB productDB = new ProductDB();
                Product product = productDB.selectByID(rs.getInt("productID"));
                WarehouseDB warehouseDB = new WarehouseDB();
                Warehouse warehouse = warehouseDB.selectByID(rs.getInt("warehouseID"));
                resultList.add( new Stock(
                        rs.getInt("quantity"),
                        rs.getInt("minQuantity"),
                        product,
                        warehouse));
            }
            return resultList;

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * @inheritDoc
     * @param value it's the given T type object (in this case Stock)
     * @return the number of rows affected by the update
     * @see DBConnection executeQuery() method
     */
    @Override
    public int update(Stock value) throws DataAccessException {
        String queryStock = "UPDATE Stock SET quantity=?, minQuantity=? " +
                "WHERE productID=? AND warehouseID=?;";
        // There are two keys which point to product and a warehouse.
        // Since stock is attached to only one product and one warehouse it's enough
        // We DO NOT update warehouseID nor productID
        int rows = 0;
        try (PreparedStatement s = db.getDBConn().prepareStatement(queryStock)) {
            s.setInt(1, value.getQuantity());
            s.setInt(2, value.getMinQuantity());
            s.setInt(3, value.getProduct().getId());
            s.setInt(4, value.getWarehouse().getId());
            rows = db.executeQuery(s);
            if (rows == 0) {
                if (create(value) != -1){
                    return 1;
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return rows;
    }

    /**
     * @inheritDoc
     * @param value it's the given T type object (in this case Stock)
     * @return the number of rows deleted from the table (1 or 0)
     * @see DBConnection executeQuery()
     */
    @Override
    public int delete(Stock value) throws DataAccessException {
        // Because of the tables cascade rule, we dont need to have separate stock query
        String query = "DELETE FROM Stock WHERE productID=? AND warehouseID=?;";
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, value.getProduct().getId());
            s.setInt(2, value.getWarehouse().getId());
            return db.executeQuery(s);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * @inheritDoc
     * @param warehouse
     * @return the list of stocks of the warehouse
     * @see DBConnection executeSelect() method
     */
    @Override
    public List<Stock> getStocks(Warehouse warehouse) throws DataAccessException {
        String query = "SELECT * FROM Stock s " +
            "WHERE warehouseID = ?;";


        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, warehouse.getId());
            ResultSet rs = db.executeSelect(s);

            ProductDAO productDB = new ProductDB();
            List<Stock> resultList = new LinkedList<>();

            while (rs.next()) {
                Product product = productDB.selectByID(rs.getInt("productID"));
                resultList.add(new Stock(
                        rs.getInt("quantity"),
                        rs.getInt("minQuantity"),
                        product,
                        warehouse));
            }
            return resultList;

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public int getStocksAmount(Warehouse warehouse) throws DataAccessException {
        String query = "SELECT count(*) as total FROM Stock where warehouseID=?;";

        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, warehouse.getId());

            ResultSet resultSet = db.executeSelect(s);
            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
            return 0;

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    public int getStocksBelowMinQuantityAmount(Warehouse warehouse) throws DataAccessException {
        String query = "SELECT count(*) as total from Stock where warehouseID=? and quantity > minQuantity";

        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, warehouse.getId());

            ResultSet resultSet = db.executeSelect(s);
            if (resultSet.next()) {
                return resultSet.getInt("total");
            }
            return 0;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }
}
