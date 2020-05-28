package database;

import model.*;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * This class is used in connection with the DAO pattern
 */
public class WarehouseOrderDB implements WarehouseOrderDAO {

    /**
     * This method takes a WarehouseOrder and converts it to a valid SQL INSERT query, which is the executed
     * @param value it's the given WarehouseOrder Model
     * @return the generated key after the insertion to the DB
     * @see DBConnection executeInsertWithID() method
     */
    public int create(WarehouseOrder value) throws DataAccessException {
        DBConnection dbConn = DBConnection.getInstance();
        Connection con = dbConn.getDBConn();

        String pstmtString = "INSERT INTO WarehouseOrder (providerID, warehouseID, date, status) VALUES (?, ?, ?, ?) ;";

        try (PreparedStatement pstmt = con.prepareStatement(pstmtString, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, value.getProvider().getId());
            pstmt.setInt(2, value.getWarehouse().getId());
            pstmt.setTimestamp(3, Timestamp.valueOf(value.getDate()));
            pstmt.setString(4, value.getStatus().name());


            int id = dbConn.executeInsertWithID(pstmt);

            insertWarehouseOrderItems(value.getItems(), id);
            insertWarehouseOrderRevision(value.getRevisions(), id);

            return id;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * This method takes an ID and converts it to a valid SQL SELECT query, which is the executed
     * @param id is the ID which we want to search for in the database
     * @return the single WarehouseOrder with the given ID
     * @see DBConnection executeSelect() method
     */
    public WarehouseOrder selectByID(int id) throws DataAccessException {
        DBConnection dbConn = DBConnection.getInstance();
        Connection con = dbConn.getDBConn();

        String query = "SELECT * FROM WarehouseOrder WHERE id=?";
        String itemQuery = "select * from WarehouseOrderItem where orderID=?";

        try (PreparedStatement s = con.prepareStatement(query)) {
            s.setInt(1, id);

            ResultSet rs = dbConn.executeSelect(s);

            if (rs.next()) {
                DAOInterface<Warehouse> warehouseDAO = new WarehouseDB();
                DAOInterface<Provider> providerDAO = new ProviderDB();
                Warehouse warehouse = warehouseDAO.selectByID(rs.getInt("warehouseID"));
                Provider provider = providerDAO.selectByID(rs.getInt("providerID"));

                List<WarehouseOrderItem> warehouseOrderItems = new LinkedList<>();

                WarehouseOrder order = new WarehouseOrder(
                        rs.getInt("id"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        Status.valueOf(rs.getString("status")),
                        warehouse,
                        provider,
                        warehouseOrderItems,
                        new LinkedList<>()
                );

                try (PreparedStatement statement = con.prepareStatement(itemQuery)) {
                    statement.setInt(1, order.getId());
                    ProductDAO productDAO = new ProductDB();
                    ResultSet resultSet = dbConn.executeSelect(statement);
                    while (resultSet.next()) {
                        // TODO rs.getInt("productID") throws exc for some reason
                        WarehouseOrderItem orderItem = new WarehouseOrderItem(
                                resultSet.getInt("quantity"),
                                resultSet.getDouble("unitPrice"),
                                productDAO.selectByID(resultSet.getInt("productID"))
                        );
                        warehouseOrderItems.add(orderItem);
                    }
                }
                order.setItems(warehouseOrderItems);
                return order;
            }
        }
        catch (Exception e) {
        	throw new DataAccessException(e.getMessage());
        }

        return null;
    }

	@Override
	public List<WarehouseOrder> all() throws DataAccessException {
        DBConnection dbConn = DBConnection.getInstance();

        String query = "SELECT * FROM WarehouseOrder";
        List<WarehouseOrder> orders = new LinkedList<>();
        DAOInterface<Provider> providerDAO = new ProviderDB();
        DAOInterface<Warehouse> warehouseDAO = new WarehouseDB();
        try (PreparedStatement s = dbConn.getDBConn().prepareStatement(query))
        {
            ResultSet rs = dbConn.executeSelect(s);
            while (rs.next()) {
                WarehouseOrder order = new WarehouseOrder(
                        rs.getInt("id"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        Status.valueOf(rs.getString("status")),
                        warehouseDAO.selectByID(rs.getInt("warehouseId")),
                        providerDAO.selectByID(rs.getInt("providerId")),
                        new LinkedList<>(),
                        new LinkedList<>()
                );
                // Leave commented for better preformance
                //order.setRevisions(getWarehouseOrderRevisions(order));
                //order.setItems(getWarehouseOrderItems(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return orders;
    }

    /**
     * This method takes an object and converts it to a valid SQL UPDATE query, which is the executed
     * @param value it's the given T type object
     * @return the number of rows affected by the update
     * @see DBConnection executeQuery() method
     */
    public int update(WarehouseOrder value) throws DataAccessException {
        DBConnection dbConn = DBConnection.getInstance();
        Connection con = dbConn.getDBConn();

        String query = "UPDATE WarehouseOrder SET date = ?, status = ? WHERE id=?;";

        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(value.getDate()));
            pstmt.setString(2, value.getStatus().name());
            pstmt.setInt(3, value.getId());

            int updated = dbConn.executeQuery(pstmt);
            if (updated == 1) {
                insertWarehouseOrderItems(value.getItems(), value.getId());
                insertWarehouseOrderRevision(value.getRevisions(), value.getId());
            }

            return updated;
        } catch (Exception e) {
            throw new DataAccessException();
        }
    }

    /**
     * This method takes an object and converts it to a valid SQL DELETE query, which is the executed
     * @param value it's the given WarehouseOrder
     * @return the number of rows deleted from the table
     * @see DBConnection executeQuery()
     */
    public int delete(WarehouseOrder value) throws DataAccessException {
        DBConnection dbConn = DBConnection.getInstance();
        Connection con = dbConn.getDBConn();

        String pstmtString = "DELETE FROM WarehouseOrder WHERE id=?;";

        try (PreparedStatement pstmt = con.prepareStatement(pstmtString)) {
            pstmt.setInt(1, value.getId());

            return dbConn.executeQuery(pstmt);
        } catch (Exception e) {
            throw new DataAccessException();
        }
    }

    @Override
    public List<WarehouseOrder> getWarehouseOrders(Warehouse warehouse) throws DataAccessException {
        DBConnection dbConn = DBConnection.getInstance();

        String query = "SELECT * FROM WarehouseOrder " +
                "WHERE warehouseID=?;";
        List<WarehouseOrder> orders = new LinkedList<>();
        DAOInterface<Provider> providerDAO = new ProviderDB();
        try (PreparedStatement s = dbConn.getDBConn().prepareStatement(query))
        {
            s.setInt(1, warehouse.getId());

            ResultSet rs = dbConn.executeSelect(s);
            while (rs.next()) {
                WarehouseOrder order = new WarehouseOrder(
                        rs.getInt("id"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        Status.valueOf(rs.getString("status")),
                        warehouse,
                        providerDAO.selectByID(rs.getInt("providerId")),
                        new LinkedList<>(),
                        new LinkedList<>()
                );
                // Leave commented for better preformance
                //order.setRevisions(getWarehouseOrderRevisions(order));
                //order.setItems(getWarehouseOrderItems(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return orders;
    }

    @Override
    public List<WarehouseOrder> getWarehouseOrders(Provider provider) throws DataAccessException {
        DBConnection dbConn = DBConnection.getInstance();

        String query = "SELECT * FROM WarehouseOrder " +
                "WHERE providerID=?;";
        List<WarehouseOrder> orders = new LinkedList<>();
        DAOInterface<Warehouse> warehouseDAO = new WarehouseDB();
        try (PreparedStatement s = dbConn.getDBConn().prepareStatement(query))
        {
            s.setInt(1, provider.getId());

            ResultSet rs = dbConn.executeSelect(s);
            while (rs.next()) {
                WarehouseOrder order = new WarehouseOrder(
                        rs.getInt("id"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        Status.valueOf(rs.getString("status")),
                        warehouseDAO.selectByID(rs.getInt("warehouseID")),
                        provider,
                        new LinkedList<>(),
                        new LinkedList<>()
                );
                // Leave commented for better preformance
                //order.setRevisions(getWarehouseOrderRevisions(order));
                //order.setItems(getWarehouseOrderItems(order.getId()));
                orders.add(order);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return orders;
    }

    @Override
    public List<WarehouseOrderItem> getWarehouseOrderItems(int warehouseOrderID) throws DataAccessException {
        DBConnection dbConn = DBConnection.getInstance();

        String query = "SELECT * FROM WarehouseOrderItem WHERE orderID=?";
        ProductDB productDB = new ProductDB();
        List<WarehouseOrderItem> items = new LinkedList<>();
        try (PreparedStatement s = dbConn.getDBConn().prepareStatement(query)) {
            s.setInt(1, warehouseOrderID);
            ResultSet rs = dbConn.executeSelect(s);
            while (rs.next()) {
                WarehouseOrderItem item = new WarehouseOrderItem(
                        rs.getInt("quantity"),
                        rs.getDouble("unitPrice"),
                        productDB.selectByID(rs.getInt("productID"))
                );
                items.add(item);
            }
            return items;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public List<WarehouseOrderRevision> getWarehouseOrderRevisions(WarehouseOrder warehouseOrder) throws DataAccessException {
        DBConnection dbConn = DBConnection.getInstance();

        String query = "SELECT * FROM WarehouseOrderRevision WHERE orderID=?";
        List<WarehouseOrderRevision> items = new LinkedList<>();
        try (PreparedStatement s = dbConn.getDBConn().prepareStatement(query)) {
            s.setInt(1, warehouseOrder.getId());
            ResultSet rs = dbConn.executeSelect(s);
            while (rs.next()) {
                WarehouseOrderRevision item = new WarehouseOrderRevision(
                        rs.getInt("id"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        rs.getString("note"),
                        Status.valueOf(rs.getString("status")),
                        warehouseOrder
                );
                items.add(item);
            }
            return items;

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public int insertWarehouseOrderItems(List<WarehouseOrderItem> warehouseOrderItems, int warehouseOrderId) throws DataAccessException {
        DBConnection dbConn = DBConnection.getInstance();
        int changes = 0;

        String updateQuery = "update WarehouseOrderItem set quantity=?, unitPrice=? where orderID=? and productID=?";
        String insertQuery = "insert into WarehouseOrderItem (orderID, productID, quantity, unitPrice) " +
                "VALUES (?, ?, ?, ?);";

        try (PreparedStatement statement = dbConn.getDBConn().prepareStatement(updateQuery)){
            for (WarehouseOrderItem warehouseOrderItem : warehouseOrderItems) {
                statement.setInt(1, warehouseOrderItem.getQuantity());
                statement.setDouble(2, warehouseOrderItem.getUnitPrice());
                statement.setInt(3, warehouseOrderId);
                statement.setInt(4, warehouseOrderItem.getProduct().getId());

                int updated = dbConn.executeQuery(statement);
                   try (PreparedStatement statement1 = dbConn.getDBConn().prepareStatement(insertQuery)){

                        statement1.setInt(1, warehouseOrderId);
                        statement1.setInt(2, warehouseOrderItem.getProduct().getId());
                        statement1.setInt(3, warehouseOrderItem.getQuantity());
                        statement1.setDouble(4, warehouseOrderItem.getUnitPrice());

                        changes += dbConn.executeQuery(statement1);
                        changes += updated;

                       return changes;
                   }
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return -1;
    }

    @Override
    public int insertWarehouseOrderRevision(List<WarehouseOrderRevision> warehouseOrderRevisions, int warehouseOrderId) throws DataAccessException {
        DBConnection dbConn = DBConnection.getInstance();

        String insertQuery = "insert into WarehouseOrderRevision (orderID, status, date, note)" +
                "VALUES (?, ?, ?, ?);";

            for (WarehouseOrderRevision orderRevision : warehouseOrderRevisions) {
                if (orderRevision.getId() == 0) {
                    try (PreparedStatement statement = dbConn.getDBConn().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

                        statement.setInt(1, warehouseOrderId);
                        statement.setString(2, orderRevision.getStatus().toString());
                        statement.setTimestamp(3, Timestamp.valueOf(orderRevision.getDate()));
                        statement.setString(4, orderRevision.getNote());
                        return dbConn.executeInsertWithID(statement);
                    } catch (SQLException e) {
                        throw new DataAccessException(e.getMessage());
                    }
                }
            }
            return -1;

    }

}
