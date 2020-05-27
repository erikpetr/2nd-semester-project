package model;

public class User {

    protected int id;
    protected String name;
    protected String password;
    protected String email;
    protected Address address;

    public User(int id, String name, String password, String email, Address address)
    {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
        this.address = address;
    }
    
    public User() {
    	
    }

    public User(String name, String password, String email, Address address)
    {
        this.name = name;
        this.password = password;
        this.email = email;
        this.address = address;
    }

    public int getId(){
        return id;
    }

    public String getName(){
        return name;
    }

    public String getPassword(){
        return password;
    }

    public String getEmail(){
        return email;
    }

    public Address getAddress() {
        return address;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setId(int id) { this.id = id; }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setEmail(String email ) {
        this.email  = email ;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    @Override
    public String toString() {
        return this.name;
    }
}

