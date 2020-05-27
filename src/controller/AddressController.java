package controller;

import database.AddressDB;
import database.DAOInterface;
import database.DataAccessException;
import model.Address;

import java.util.List;

public class AddressController {
    private final DAOInterface<Address> addressDB;

    public AddressController() throws DataAccessException {
        addressDB = new AddressDB();
    }

    public int createAddress(Address address) throws DataAccessException {
        return addressDB.create(address);
    }

    public int updateAddress(Address address) throws DataAccessException {
        return addressDB.update(address);
    }

    public int deleteAddress(Address address) throws DataAccessException {
        return addressDB.delete(address);
    }

    public Address getAddressById(int id) throws DataAccessException {
        return addressDB.selectByID(id);
    }

    public List<Address> getAddresses() throws DataAccessException {
        return addressDB.all();
    }

}
