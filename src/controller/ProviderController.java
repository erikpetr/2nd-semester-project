package controller;

import database.DAOInterface;
import database.DataAccessException;
import database.ProviderDB;
import model.Provider;

import java.util.List;

public class ProviderController {
    private final DAOInterface<Provider> providerDAO;


    public ProviderController() throws DataAccessException {
        providerDAO = new ProviderDB();
    }

    public int createProvider(Provider value) throws DataAccessException {
        return providerDAO.create(value);
    }

    public Provider getProviderByID(int id) throws DataAccessException {
        return providerDAO.selectByID(id);
    }

    public int deleteProvider(Provider value) throws DataAccessException {
        return providerDAO.delete(value);
    }

    public List<Provider> all() throws DataAccessException {
        return providerDAO.all();
    }

    public int updateProvider(Provider value) throws DataAccessException {
        return providerDAO.update(value);
    }
}
