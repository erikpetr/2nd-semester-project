package controller;

import database.*;
import model.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Controller class which connects the GUI with the DAO
 *
 * @author dmai0919-group3@UCNDK.onmicrosoft.com
 */
public class OrderController {
    private Order order;
	private final OrderDAO orderDAO;
	
	public OrderController() throws DataAccessException
	{
		 this.orderDAO = new OrderDB();
	}


    /**
     * Creates a new order for a given store and warehouse
     *
     * @param store     the store for the order
     * @param warehouse the warehouse for the order
     */
    public void createOrder(Store store, Warehouse warehouse) throws ControlException {
        if (store == null || warehouse == null) {
            throw new ControlException("Provide valid information");
        } else {
            this.order = new Order(store, warehouse);
        }
    }

    /**
     * Decrements amount of products in OrderItem
     * or removes OrderItem if amount is bigger than quantity
     *
     * @param product the product to be removed
     * @param amount  the amount of products to be removed
     * @return boolean
     */
    public boolean removeProduct(Product product, int amount) {
        if (order == null || product == null || amount <= 0) {
            throw new IllegalStateException("There's no Order object initialized. Please call createOrder() method first.");
        }

        for (OrderItem orderItem : order.getItems()) {
            if (orderItem.getProduct().equals(product)) {
                if (orderItem.getQuantity() > amount) {
                    orderItem.setQuantity(orderItem.getQuantity() - amount);
                } else {
                    order.removeOrderItem(orderItem);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the whole product from the order
     *
     * @param product the product to be removed
     * @return the value of {@link Order#removeOrderItem(OrderItem)}
     */
    public boolean removeProduct(Product product) {
        for (OrderItem orderItem : order.getItems()) {
            if (product.equals(orderItem.getProduct())) {
                return order.removeOrderItem(orderItem);
            }
        }
        return false;
    }

    /*
     *  Get all orders belonging to logged in user
     */
    public List<Order> getOrders() throws ControlException {
        try {
            OrderDAO orderDAO = new OrderDB();
            User loggedInUser = LoginController.getLoggedInUser();
            if (loggedInUser instanceof Warehouse) {
                return orderDAO.getOrders((Warehouse) LoginController.getLoggedInUser());
            } else if (loggedInUser instanceof Store){
                return orderDAO.getOrders((Store) LoginController.getLoggedInUser());
            }
            return null;
        } catch (DataAccessException e) {
            throw new ControlException(e.getMessage());
        }
    }

    /**
     * Adds a given amount of products to the order
     *
     * @param product the product to be added
     * @param amount  the amount of products to be added
     * @return the return value of {@link Order#addOrderItem(OrderItem)}
     */
    public boolean addProduct(Product product, int amount) throws ControlException {
        if (order == null || product == null || amount <= 0) {
            throw new IllegalStateException("There's no Order object initialized. Please call createOrder() method first.");
        }
        ProductDAO productDAO;
        try {
            productDAO = new ProductDB();
            for (OrderItem orderItem : order.getItems()) {
                if (orderItem.getProduct().getId() == product.getId()) {
                    if (productDAO.checkAvailability(order.getWarehouse(), product, amount + orderItem.getQuantity())) {
                        orderItem.setQuantity(amount + orderItem.getQuantity());
                        return true;
                    } else {
                        throw new ControlException("There are not enough items in stock.\nPlease enter a smaller amount.");
                    }
                }
            }
            if (productDAO.checkAvailability(order.getWarehouse(), product, amount)) {
                OrderItem orderItem = new OrderItem(product, product.getPrice(), amount);
                return order.addOrderItem(orderItem);
            } else {
                throw new ControlException("There are not enough items in stock.\nPlease enter a smaller amount.");
            }
        } catch (DataAccessException e) {
            throw new ControlException("Unable to connect to server.\n" + e.getMessage());
        }
    }

    public Order getOrder(int id) throws DataAccessException 
    {
        return orderDAO.selectByID(id);
    }

    public Order getOrder() {
        return order;
    }

    /**
     * @param order
     */
    public void setOrder(Order order) {
        this.order = order;
    }

    /**
     * @return int
     */
    public int finishOrder() throws ControlException {
        if (order == null) {
            throw new ControlException("Create Order first");
        }

        if (order.getItems().isEmpty()) {
            throw new ControlException("Please add items to your order");
        }
        order.calculateTotalPrice();

        OrderRevision revision = new OrderRevision(
                LocalDateTime.now(),
                Status.PENDING,
                "Order created",
                order,
                order.getItems()
        );

        order.addRevision(revision);
        order.setDate(LocalDateTime.now());
        order.setStatus(Status.PENDING);

        try {
            OrderDAO orderDAO = new OrderDB();

            return orderDAO.create(order);
        } catch (DataAccessException e) {
            throw new ControlException(e.getMessage());
        }
    }

    public boolean updateOrder(Order order) throws ControlException {
        try {
            if (orderDAO.update(order) != 0) {
                return true;
            } else {
                return false;
            }
        } catch (DataAccessException e) {
            e.printStackTrace();
            throw new ControlException("Can't update order");
        }
    }

    // Call this method ONLY when Warehouse Manager approves order
    public boolean updateStock(Order order) throws ControlException {
        StockDAO stockDAO = null;
        try {
            stockDAO = new StockDB();

            for (OrderItem orderItem : order.getItems()) {
                Stock stock = stockDAO.getStock(order.getWarehouse().getId(), orderItem.getProduct().getId());
                int quantity = stock.getQuantity();
                quantity = quantity - orderItem.getQuantity();
                stock.setQuantity(quantity);
                stockDAO.update(stock);
            }
        } catch (DataAccessException e) {
            throw new ControlException(e.getMessage());
        }
        return true;
    }
}
