package database;

import model.*;

import java.util.List;

/**
 * This DAO interface is used by the OrderController
 *
 * @param <T> is the return type of the methods inside this interface
 * @see controller.OrderController
 */
public interface OrderDAO extends DAOInterface<Order> {
    List<Order> getOrders(Warehouse warehouse) throws DataAccessException;

    List<Order> getOrders(Store store) throws DataAccessException;

    List<OrderItem> getOrderItems(int orderID) throws DataAccessException;

    List<OrderRevision> getOrderRevisions(Order order) throws DataAccessException;

    List<OrderItem> getOrderRevisionItems(int orderRevisionId) throws DataAccessException;

    int insertOrderRevision(List<OrderRevision> orderRevisions, int orderId) throws DataAccessException;

    int insertOrderRevisionItems(List<OrderItem> orderItems, int revisionId) throws DataAccessException;

    int getOrdersAmount(User user) throws DataAccessException;

    int getPendingOrdersAmount(User user) throws DataAccessException;

    Status getOrderStatus(int orderId) throws  DataAccessException;
}
