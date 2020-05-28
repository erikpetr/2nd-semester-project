package controller;

import database.*;
import model.*;

import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

public class WarehouseOrderController {
    private final WarehouseOrderDAO warehouseOrderDAO;
    private WarehouseOrder warehouseOrder;

    public WarehouseOrderController() throws DataAccessException {
        this.warehouseOrderDAO = new WarehouseOrderDB();
    }

    /**
     * Creates a new warehouse order for a given store and warehouse
     *
     * @param warehouse the warehouse for the warehouse order
     */

    public void createWarehouseOrder(Warehouse warehouse) throws ControlException {
        if (warehouse == null) {
            throw new ControlException("Provide valid information");
        } else {
            this.warehouseOrder = new WarehouseOrder(warehouse);
            this.warehouseOrder.setStatus(Status.PENDING);
        }
    }

    public boolean removeProduct(Product product, int amount) {
        if (warehouseOrder == null || product == null || amount <= 0) {
            throw new IllegalStateException("There's no Warehouse order object initialized. Please call createWarehouseOrder() method first.");
        }

        for (WarehouseOrderItem warehouseOrderItem : warehouseOrder.getItems()) {
            if (warehouseOrderItem.getProduct().equals(product)) {
                if (warehouseOrderItem.getQuantity() > amount) {
                    warehouseOrderItem.setQuantity(warehouseOrderItem.getQuantity() - amount);
                } else {
                    warehouseOrder.removeWarehouseOrderItem(warehouseOrderItem);
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Removes the whole product from the warehouse order
     *
     * @param product the product to be removed
     * @return the value of {@link WarehouseOrder#removeWarehouseOrderItem(WarehouseOrderItem)}
     */
    public boolean removeProduct(Product product) {
        for (WarehouseOrderItem warehouseOrderItem : warehouseOrder.getItems()) {
            if (product.equals(warehouseOrderItem.getProduct())) {
                return warehouseOrder.removeWarehouseOrderItem(warehouseOrderItem);
            }
        }
        return false;
    }

    /*
     *  Get all orders belonging to logged in user
     */
    public List<WarehouseOrder> getWarehouseOrders() throws ControlException {
        try {
            WarehouseOrderDAO warehouseOrderDAO = new WarehouseOrderDB();
            User loggedInUser = LoginController.getLoggedInUser();
            if (loggedInUser instanceof Warehouse) {
                return warehouseOrderDAO.getWarehouseOrders((Warehouse) LoginController.getLoggedInUser());
            }
            return new LinkedList<>();
        } catch (DataAccessException e) {
            throw new ControlException(e.getMessage());
        }
    }

    public boolean addProduct(Product product, double unitPrice, int amount) throws ControlException {
        if (warehouseOrder == null || product == null || amount <= 0) {
            throw new IllegalStateException("There's no Warehouse Order object initialized. Please call createWarehouseOrder() method first.");
        }
        WarehouseOrderItem warehouseOrderItem = new WarehouseOrderItem(amount, unitPrice, product);
        return warehouseOrder.addWarehouseOrderItem(warehouseOrderItem);
    }

    public WarehouseOrder getWarehouseOrder(int id) throws DataAccessException {
        return warehouseOrderDAO.selectByID(id);
    }

    public void addProvider(Provider provider) {
        warehouseOrder.setProvider(provider);
    }

    public int finishWarehouseOrder() throws ControlException {
        if (warehouseOrder == null) {
            throw new ControlException("Create Warehouse Order first");
        }

        if (warehouseOrder.getItems().isEmpty()) {
            throw new ControlException("Please add items to your warehouse order");
        }
        warehouseOrder.calculateTotalPrice();

        WarehouseOrderRevision revision = new WarehouseOrderRevision(
                LocalDateTime.now(),
                Status.PENDING,
                "Warehouse Order created",
                warehouseOrder
        );

        warehouseOrder.addRevision(revision);
        warehouseOrder.setDate(LocalDateTime.now());
        warehouseOrder.setStatus(Status.PENDING);

        try {
            WarehouseOrderDAO warehouseOrderDAO = new WarehouseOrderDB();

            return warehouseOrderDAO.create(warehouseOrder);
        } catch (DataAccessException e) {
            throw new ControlException(e.getMessage());
        }
    }

    public boolean updateWarehouseOrder(WarehouseOrder warehouseOrder) throws ControlException {
        try {
            return (warehouseOrderDAO.update(warehouseOrder) != 0);
        } catch (DataAccessException e) {
            throw new ControlException("Can't update warehouse order\n" + e.getMessage());
        }
    }

    public boolean updateStock(WarehouseOrder warehouseOrder) throws ControlException {
        StockDAO stockDAO = null;
        try {
            stockDAO = new StockDB();

            for (WarehouseOrderItem warehouseOrderItem : warehouseOrder.getItems()) {
                Stock stock = stockDAO.getStock(warehouseOrder.getWarehouse().getId(), warehouseOrderItem.getProduct().getId());
                int quantity = stock.getQuantity();
                quantity = quantity + warehouseOrderItem.getQuantity();
                stock.setQuantity(quantity);
                stockDAO.update(stock);
            }
        } catch (DataAccessException e) {
            throw new ControlException(e.getMessage());
        }
        return true;
    }

    public WarehouseOrder getWarehouseOrder() {
        return warehouseOrder;
    }

    public void setWarehouseOrder(WarehouseOrder warehouseOrder) {
        this.warehouseOrder = warehouseOrder;
    }
}
