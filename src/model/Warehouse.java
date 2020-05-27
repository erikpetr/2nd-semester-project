package model;

public class Warehouse extends User {

    public Warehouse(int id, String name, String password, String email, Address address) {
        super(id, name, password, email, address);
    }

    public Warehouse(String name, String password, String email, Address address) {
        super(name, password, email, address);
    }

    @Override
    public String toString() {
        return getName() +
                " ("
                + getAddress().getCity() + ", "
                + getAddress().getStreet() +
                ")";
    }
}
