package model;

public class Product {

    private int id;
    private String name;
    // TODO: REfactor to double
    private double weight;
    private double price;

    public Product(int id, String name, double weight, Double price)
    {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.price = price;
    }

    public Product(String name, double weight, double price)
    {
        this.name = name;
        this.weight = weight;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getWeight() {
        return weight;
    }

    public double getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    
    @Override
    public String toString() {
        return name + " (" + price + " EUR)";
    }

}

