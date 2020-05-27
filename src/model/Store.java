package model;

public class Store extends User {

    public Store(int id, String name, String password, String email, Address address) {
        super(id, name, password, email, address);
    }

    public Store(String name, String password, String email, Address address) {
        super(name, password, email, address);
    }

    @Override
    public String toString() {
        return getName() + " (" + getAddress().getCity() + ", " + getAddress().getStreet() + ")";
    }
}


