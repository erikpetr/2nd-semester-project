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

        // TODO: Insert orderItems and revisions too
        try (PreparedStatement pstmt = con.prepareStatement(pstmtString, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setInt(1, value.getProvider().getId());
            pstmt.setInt(2, value.getWarehouse().getId());
            pstmt.setTimestamp(2, Timestamp.valueOf(value.getDate()));
            pstmt.setString(3, value.getStatus().name());

            return dbConn.executeQuery(pstmt);
        } catch (Exception e) {
            throw new DataAccessException();
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
        String itemQuery = "select * from WarehouseOrderItem as w, Product as p " +
                "JOIN Product on w.productID=p.id where orderID=?";

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
                        warehouseOrderItems
                );

                PreparedStatement statement = con.prepareStatement(itemQuery);
                statement.setInt(1, order.getId());
                ResultSet resultSet = dbConn.executeSelect(statement);
                while (resultSet.next()) {
                    WarehouseOrderItem orderItem = new WarehouseOrderItem(
                            resultSet.getInt("w.quantity"),
                            resultSet.getDouble("w.unitPrice"),
                            new Product(
                                    resultSet.getInt("p.id"),
                                    resultSet.getString("p.name"),
                                    resultSet.getDouble("p.weight"),
                                    resultSet.getDouble("p.price")
                            )
                    );
                    warehouseOrderItems.add(orderItem);
                }

                order.setItems(warehouseOrderItems);
                return order;
            }
        }
        catch (Exception e) {
        	throw new DataAccessException();
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
                        null
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

        // TODO: Also WarehousOrderRevision should be added when update happened
        try (PreparedStatement pstmt = con.prepareStatement(query)) {
            pstmt.setTimestamp(1, Timestamp.valueOf(value.getDate()));
            pstmt.setString(2, value.getStatus().name());
            pstmt.setInt(3, value.getId());

            return dbConn.executeQuery(pstmt);
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
                        null
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
                        null
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
    public int insertWarehouseOrderRevision(List<WarehouseOrderRevision> warehouseOrderRevisions, int warehouseOrderId) throws DataAccessException {
        DBConnection dbConn = DBConnection.getInstance();

        String insertQuery = "insert into WarehouseOrderRevision (orderID, status, date, note)" +
                "VALUES (?, ?, ?, ?);";

        try {
            for (WarehouseOrderRevision orderRevision : warehouseOrderRevisions) {
                if (orderRevision.getId() == 0) {
                    PreparedStatement statement = dbConn.getDBConn().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);

                    statement.setInt(1, warehouseOrderId);
                    statement.setString(2, orderRevision.getStatus().toString());
                    statement.setTimestamp(3, Timestamp.valueOf(orderRevision.getDate()));
                    statement.setString(4, orderRevision.getNote());

                    return dbConn.executeInsertWithID(statement);
                }
            }
            return -1;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

}
