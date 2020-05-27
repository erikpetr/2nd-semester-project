package model;

public class StoreStockReportItem {

    private int quantity;
    private Product product;

    public StoreStockReportItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
    }

    public int getQuantity(){
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    @Override
    public String toString() {
        return this.product.getName() + " (" + this.quantity + " pcs)";
    }
}

