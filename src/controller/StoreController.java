package controller;

import database.DAOInterface;
import database.DataAccessException;
import database.StoreDB;
import model.Address;
import model.Store;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller class which connects the GUI with the DAO
 *
 * @author dmai0919-group3@UCNDK.onmicrosoft.com
 */

public class StoreController {
    private DAOInterface<Store> storeDB;

    public StoreController() throws DataAccessException {
        storeDB = new StoreDB();
    }

    public int createStore(Store store) throws DataAccessException {
        return storeDB.create(store);
    }

    public boolean updateStore(Store store) throws DataAccessException {
        if (storeDB.update(store) == 1) {
            return true;
        }
        else { return false;}
    }

    public int deleteStore(Store store) throws DataAccessException {
        int rowsAltered = storeDB.delete(store);;
        return rowsAltered;
    }

    public List<Store> getStores() throws DataAccessException {
        return storeDB.all();
    }
    public Store getStoreByID(int id) throws DataAccessException {
        return storeDB.selectByID(id);
    }

}
