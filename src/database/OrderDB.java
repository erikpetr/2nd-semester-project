package database;

import model.*;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

/**
 * DAO class for Order via OrderDAO
 *
 * @author dmai0919-group3@UCNDK.onmicrosoft.com
 */
public class OrderDB implements OrderDAO {

    DBConnection db = DBConnection.getInstance();

    /**
     * Default empty constructor so we can pass along the DataAccessException from DBConnection
     *
     * @throws DataAccessException
     */
    public OrderDB() throws DataAccessException {
        //This constructor is empty because it only exists to pass along DataAccessException from DBConnection.getInstance()
    }


    /**
     * Takes an existing Order object and inserts it into the DB
     *
     * @param value it's the given Order object
     * @return the ID of the inserted Order
     * @throws DataAccessException when there is an SQLException caught inside the method
     */
    @Override
    public int create(Order value) throws DataAccessException {
        String query = "INSERT INTO [Order] (storeID, warehouseID, status, date) VALUES (?,?,?,?);";
        String orderItemQuery = "insert into OrderItem (orderID, quantity, unitPrice, productID, orderRevisionID) VALUES (?, ?, ?, ?, ?);";
        String orderRevisionQuery = "insert into OrderRevision (orderID, status, date, note)" +
                "VALUES (?, ?, ?, ?);";

        int orderID = -1;
        try (PreparedStatement s = db.getDBConn().prepareStatement(query, Statement.RETURN_GENERATED_KEYS)) {
            s.setInt(1, value.getStore().getId());
            s.setInt(2, value.getWarehouse().getId());
            s.setString(3, value.getStatus().toString());
            s.setTimestamp(4, Timestamp.valueOf(value.getDate()));

            orderID = db.executeInsertWithID(s);

            for (OrderItem orderItem : value.getItems()) {
                try (PreparedStatement orderItemStatement = db.getDBConn().prepareStatement(orderItemQuery)) {
                    orderItemStatement.setInt(1, orderID);
                    orderItemStatement.setInt(2, orderItem.getQuantity());
                    orderItemStatement.setDouble(3, orderItem.getUnitPrice());
                    orderItemStatement.setInt(4, orderItem.getProduct().getId());
                    orderItemStatement.setString(5, null);

                    db.executeQuery(orderItemStatement);
                }
            }

            for (OrderRevision revision : value.getRevisions()) {
                try (PreparedStatement revisionStatement = db.getDBConn().prepareStatement(orderRevisionQuery, Statement.RETURN_GENERATED_KEYS)) {

                    revisionStatement.setInt(1, orderID);
                    revisionStatement.setString(2, revision.getStatus().toString());
                    revisionStatement.setTimestamp(3, Timestamp.valueOf(revision.getDate()));
                    revisionStatement.setString(4, revision.getNote());

                    int revisionId = db.executeInsertWithID(revisionStatement);

                    for (OrderItem orderItem : value.getItems()) {
                        try (PreparedStatement orderItemStatement = db.getDBConn().prepareStatement(orderItemQuery)) {
                            orderItemStatement.setString(1, null);
                            orderItemStatement.setInt(2, orderItem.getQuantity());
                            orderItemStatement.setDouble(3, orderItem.getUnitPrice());
                            orderItemStatement.setInt(4, orderItem.getProduct().getId());
                            orderItemStatement.setInt(5, revisionId);

                            db.executeQuery(orderItemStatement);

                        }
                    }
                }
            }

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }

        return orderID;
    }

    /**
     * Finds an order with a given ID
     *
     * @param id is the ID which we want to search for in the database
     * @return the Order object with the given ID or null if it doesn't exist
     * @throws DataAccessException when there is an SQLException caught inside the method
     */
    @Override
    public Order selectByID(int id) throws DataAccessException {
        DAOInterface<Store> storeDAOInterface = new StoreDB();
        DAOInterface<Warehouse> warehouseDAOInterface = new WarehouseDB();

        String query = "SELECT TOP 1 * FROM [Order] WHERE id=?;";
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, id);
            ResultSet rs = db.executeSelect(s);
            if (rs.next()) {
                Order order = new Order(
                        rs.getInt("id"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        Status.valueOf(rs.getString("status")),
                        warehouseDAOInterface.selectByID(rs.getInt("warehouseID")),
                        storeDAOInterface.selectByID(rs.getInt("storeID")),
                        getOrderItems(id),
                        null);
                order.setRevisions(getOrderRevisions(order));
                return order;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return null;
    }

    /**
     * Takes a given Order object and updates it in the DB
     *
     * @param value it's the given Order object
     * @return the number of rows changed in the DB
     * @throws DataAccessException when there is an SQLException caught inside the method
     */
    @Override
    public int update(Order value) throws DataAccessException {
        String query = "UPDATE [Order] SET storeID=?, warehouseID=?, date=?, status=? WHERE id=?;";
        String queryItems = "UPDATE OrderItem SET quantity=? WHERE OrderID=? AND ProductID=?";
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, value.getStore().getId());
            s.setInt(2, value.getWarehouse().getId());
            s.setTimestamp(3, Timestamp.valueOf(value.getDate()));
            s.setString(4, value.getStatus().toString());
            s.setInt(5, value.getId());
            int rows = db.executeQuery(s);
            for (OrderItem item : value.getItems()) {
                try (PreparedStatement ps = db.getDBConn().prepareStatement(queryItems)) {
                    ps.setInt(1, item.getQuantity());
                    ps.setInt(2, value.getId());
                    ps.setInt(3, item.getProduct().getId());
                    rows += db.executeQuery(ps);
                }
            }
            insertOrderRevision(value.getRevisions(), value.getId());
            return rows;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Takes an Order object and deletes it from the Database
     *
     * @param value it's the given Order object
     * @return The number of rows affected
     * @throws DataAccessException when there is an SQLException caught inside the method
     */
    @Override
    public int delete(Order value) throws DataAccessException {
        String query = "DELETE FROM [Order] WHERE id=?";
        //We don't need OrderItem and OrderStatus queries because of the cascade rules on the tables in the DB
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, value.getId());
            return db.executeQuery(s);
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Return all orders WITHOUT ANY OrderItems or OrderRevisions
     *
     * @return a List containing all orders
     * @throws DataAccessException
     */
    @Override
    public List<Order> all() throws DataAccessException {
        DAOInterface<Store> storeDAOInterface = new StoreDB();
        DAOInterface<Warehouse> warehouseDAOInterface = new WarehouseDB();
        String query = "SELECT * FROM [Order];";
        List<Order> orders = new LinkedList<>();

        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            ResultSet rs = db.executeSelect(s);
            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("id"),
                        storeDAOInterface.selectByID(rs.getInt("storeID")),
                        warehouseDAOInterface.selectByID(rs.getInt("warehouseID")),
                        rs.getTimestamp("date").toLocalDateTime(),
                        Status.valueOf(rs.getString("status"))
                );
                orders.add(order);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return orders;
    }


    /**
     * Gets all Orders for a given Warehouse
     *
     * @param warehouse the Warehouse whose Orders we are searching for
     * @return a List containing all results
     * @throws DataAccessException when there is an SQLException caught inside the method
     */
    @Override
    public List<Order> getOrders(Warehouse warehouse) throws DataAccessException {
        String query = "SELECT * FROM [Order] " +
                "WHERE warehouseID=?;";
        List<Order> orders = new LinkedList<>();
        DAOInterface<Warehouse> warehouseDAO = new WarehouseDB();
        DAOInterface<Store> storeDAO = new StoreDB();
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, warehouse.getId());

            ResultSet rs = db.executeSelect(s);
            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("id"),
                        storeDAO.selectByID(rs.getInt("storeID")),
                        warehouseDAO.selectByID(rs.getInt("warehouseID")),
                        rs.getTimestamp("date").toLocalDateTime(),
                        Status.valueOf(rs.getString("status"))
                ));
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return orders;
    }

    /**
     * Gets all Orders for a given Warehouse
     *
     * @param store the Warehouse whose Orders we are searching for
     * @return a List containing all results
     * @throws DataAccessException when there is an SQLException caught inside the method
     */
    @Override
    public List<Order> getOrders(Store store) throws DataAccessException {
        String query = "SELECT * FROM [Order] " +
                "WHERE storeID=?;";
        List<Order> orders = new LinkedList<>();
        DAOInterface<Warehouse> warehouseDAO = new WarehouseDB();
        DAOInterface<Store> storeDAO = new StoreDB();
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, store.getId());

            ResultSet rs = db.executeSelect(s);
            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("id"),
                        storeDAO.selectByID(rs.getInt("storeID")),
                        warehouseDAO.selectByID(rs.getInt("warehouseID")),
                        rs.getTimestamp("date").toLocalDateTime(),
                        Status.valueOf(rs.getString("status"))
                );
                orders.add(order);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
        return orders;
    }

    /**
     * Finds all OrderItems for a given orderID
     *
     * @param orderID the ID of the Order we're searching
     * @return a List containing all items
     * @throws DataAccessException
     */
    @Override
    public List<OrderItem> getOrderItems(int orderID) throws DataAccessException {
        String query = "SELECT * FROM OrderItem WHERE orderID=?";
        ProductDB productDB = new ProductDB();
        List<OrderItem> items = new LinkedList<>();
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, orderID);
            ResultSet rs = db.executeSelect(s);
            while (rs.next()) {
                OrderItem item = new OrderItem(
                        productDB.selectByID(rs.getInt("productID")),
                        rs.getDouble("unitPrice"),
                        rs.getInt("quantity")
                );
                items.add(item);
            }
            return items;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    /**
     * Get all OrderRevisions for a given orderID
     *
     * @param order the Order we're searching
     * @return a List containing all revisions
     * @throws DataAccessException
     */
    @Override
    public List<OrderRevision> getOrderRevisions(Order order) throws DataAccessException {
        String query = "SELECT * FROM OrderRevision WHERE orderID=?";
        List<OrderRevision> items = new LinkedList<>();
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, order.getId());
            ResultSet rs = db.executeSelect(s);
            while (rs.next()) {
                OrderRevision item = new OrderRevision(
                        rs.getInt("id"),
                        rs.getTimestamp("date").toLocalDateTime(),
                        Status.valueOf(rs.getString("status")),
                        rs.getString("note"),
                        order,
                        null
                );
                item.setItemsChanged(getOrderRevisionItems(item.getId()));
                items.add(item);
            }
            return items;

        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public List<OrderItem> getOrderRevisionItems(int orderRevisionId) throws DataAccessException {
        String query = "SELECT * FROM OrderItem WHERE orderRevisionID=?";
        ProductDB productDB = new ProductDB();
        List<OrderItem> items = new LinkedList<>();
        try (PreparedStatement s = db.getDBConn().prepareStatement(query)) {
            s.setInt(1, orderRevisionId);
            ResultSet rs = db.executeSelect(s);
            while (rs.next()) {
                OrderItem item = new OrderItem(
                        productDB.selectByID(rs.getInt("productID")),
                        rs.getDouble("unitPrice"),
                        rs.getInt("quantity")
                );
                items.add(item);
            }
            return items;
        } catch (SQLException e) {
            throw new DataAccessException(e.getMessage());
        }
    }

    @Override
    public int insertOrderRevision(List<OrderRevision> orderRevisions, int orderId) throws DataAccessException {
        String insertQuery = "insert into OrderRevision (orderID, status, date, note)" +
                "VALUES (?, ?, ?, ?);";

        for (OrderRevision orderRevision : orderRevisions) {
            if (orderRevision.getId() == 0) { // TODO Why is this if here?
                try (PreparedStatement statement = db.getDBConn().prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS)) {

                    statement.setInt(1, orderId);
                    statement.setString(2, orderRevision.getStatus().toString());
                    statement.setTimestamp(3, Timestamp.valueOf(orderRevision.getDate()));
                    statement.setString(4, orderRevision.getNote());

                    int id = db.executeInsertWithID(statement);

                    insertOrderRevisionItems(orderRevision.getItemsChanged(), id);

                    return id;
                } catch (SQLException e) {
                    throw new DataAccessException(e.getMessage());
                }
            }
        }
        return -1;
    }

        @Override
        public int insertOrderRevisionItems (List < OrderItem > orderItems,int revisionId) throws DataAccessException {
            String insertQuery = "insert into OrderItem (orderID, quantity, unitPrice, productID, orderRevisionID) VALUES (?, ?, ?, ?, ?);";
            int updated = 0;
            for (OrderItem orderItem : orderItems) {
                    try (PreparedStatement statement = db.getDBConn().prepareStatement(insertQuery)) {

                        statement.setString(1, null);
                        statement.setInt(2, orderItem.getQuantity());
                        statement.setDouble(3, orderItem.getUnitPrice());
                        statement.setInt(4, orderItem.getProduct().getId());
                        statement.setInt(5, revisionId);

                        updated += db.executeQuery(statement);
                    } catch (SQLException throwables) {
                        throw new DataAccessException(throwables.getMessage());
                    }
            }
            return updated;
        }

        @Override
        public int getOrdersAmount (User user) throws DataAccessException {
            String query;
            if (user instanceof Warehouse) {
                query = "SELECT count(*) as total FROM [Order] where warehouseID=?";
            } else {
                query = "select count(*) as total from [Order] where storeID=?";
            }

            try (PreparedStatement statement = db.getDBConn().prepareStatement(query)){
                statement.setInt(1, user.getId());

                ResultSet resultSet = db.executeSelect(statement);
                if (resultSet.next()) {
                    return resultSet.getInt("total");
                }
                return 0;
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }

        @Override
        public int getPendingOrdersAmount (User user) throws DataAccessException {
            String query;
            if (user instanceof Warehouse) {
                query = "SELECT count(*) as total FROM [Order] where warehouseID=? and status not in ('DELIVERED', 'REJECTED')";
            } else {
                query = "select count(*) as total from [Order] where storeID=? and status not in ('DELIVERED', 'REJECTED')";
            }

            try (PreparedStatement statement = db.getDBConn().prepareStatement(query)){
                statement.setInt(1, user.getId());

                ResultSet resultSet = db.executeSelect(statement);
                if (resultSet.next()) {
                    return resultSet.getInt("total");
                }
                return 0;

            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }

        @Override
        public Status getOrderStatus ( int orderId) throws DataAccessException {
            String query = "SELECT status from [Order] where id=?;";

            try (PreparedStatement statement = db.getDBConn().prepareStatement(query)){
                statement.setInt(1, orderId);

                ResultSet resultSet = db.executeSelect(statement);
                if (resultSet.next()) {
                    return Status.valueOf(resultSet.getString("status"));
                }
                return null;
            } catch (SQLException e) {
                throw new DataAccessException(e.getMessage());
            }
        }
    }
