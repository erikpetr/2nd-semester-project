package model;

public class Product {

    private int id;
    private String name;
    private double weight;
    private double price;

    public Product(int id, String name, double weight, Double price) {
        this.id = id;
        this.name = name;
        this.weight = weight;
        this.price = price;
    }

    public Product(String name, double weight, double price) {
        this.name = name;
        this.weight = weight;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return name + " (" + price + " EUR)";
    }

}

