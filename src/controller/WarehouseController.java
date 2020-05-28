package controller;

import database.DAOInterface;
import database.DataAccessException;
import database.WarehouseDB;
import model.Warehouse;

import java.util.List;

public class WarehouseController {

    private DAOInterface<Warehouse> warehouseDAO;

    public WarehouseController() throws DataAccessException {
        warehouseDAO = new WarehouseDB();
    }

    public List<Warehouse> getWarehouses() throws DataAccessException {
        return warehouseDAO.all();
    }
}
