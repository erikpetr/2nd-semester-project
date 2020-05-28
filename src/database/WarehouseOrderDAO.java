package database;

import model.*;

import java.util.List;

public interface WarehouseOrderDAO extends DAOInterface<WarehouseOrder> {

    List<WarehouseOrder> getWarehouseOrders(Warehouse warehouse) throws DataAccessException;

    List<WarehouseOrder> getWarehouseOrders(Provider provider) throws DataAccessException;

    List<WarehouseOrderItem> getWarehouseOrderItems(int warehouseOrderID) throws DataAccessException;

    List<WarehouseOrderRevision> getWarehouseOrderRevisions(WarehouseOrder warehouseOrder) throws DataAccessException;

    int insertWarehouseOrderItems(List<WarehouseOrderItem> warehouseOrderItems, int warehouseOrderId) throws DataAccessException;

    int insertWarehouseOrderRevision(List<WarehouseOrderRevision> warehouseOrderRevisions, int warehouseOrderId) throws DataAccessException;

}
