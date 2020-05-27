package controller;

import database.DataAccessException;
import database.ProviderDB;
import model.Provider;

import java.util.List;

public class ProviderController {
    private final ProviderDB providerDB;


    public ProviderController() throws DataAccessException {
        providerDB = new ProviderDB();
    }

    public int createProvider(Provider value) throws DataAccessException {
        return providerDB.create(value);
    }

    public Provider getProviderByID(int id) throws DataAccessException {
        return providerDB.selectByID(id);
    }

    public int deleteProvider(Provider value) throws DataAccessException {
        return providerDB.delete(value);
    }

    public List<Provider> all() throws DataAccessException {
        return providerDB.all();
    }

    public int updateProvider(Provider value) throws DataAccessException {
        return providerDB.update(value);
    }
}
