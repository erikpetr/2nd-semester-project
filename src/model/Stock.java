package model;

public class Stock {
    private int quantity;
    private int minQuantity;
    private Product product;
    private Warehouse warehouse;

    public Stock(int quantity, int minQuantity, Product product, Warehouse warehouse) {
        this.quantity = quantity;
        this.minQuantity = minQuantity;
        this.product = product;
        this.warehouse = warehouse;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public void setMinQuantity(int minQuantity) {
        this.minQuantity = minQuantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    @Override
    public String toString() {
        return warehouse.toString() + " / " + product.toString() + "(" + quantity + " pcs)";
    }
}
