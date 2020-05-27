package model;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.LinkedList;
import java.util.List;

public class WarehouseOrder {

    private int id;
    private LocalDateTime date;
    private Status status;
    private Warehouse warehouse;
    private Provider provider;
    private List<WarehouseOrderItem> items;
    private List<WarehouseOrderRevision> revisions;

    public WarehouseOrder(Warehouse warehouse) {
        this.warehouse = warehouse;
        items = new LinkedList<>();
        revisions = new LinkedList<>();
    }

    public WarehouseOrder(int id, LocalDateTime date, Status status, Warehouse warehouse, Provider provider, List<WarehouseOrderItem> items)
    {
        this.id = id;
        this.date = date;
        this.status = status;
        this.warehouse = warehouse;
        this.provider = provider;
        this.items = items;
    }

    public WarehouseOrder(LocalDateTime date, Status status, Warehouse warehouse, Provider provider, List<WarehouseOrderItem> items) {
        this.date = date;
        this.status = status;
        this.warehouse = warehouse;
        this.provider = provider;
        this.items = items;
    }

    public int getId(){
        return id;
    }

    public LocalDateTime getDate(){
        return date;
    }

    public Status getStatus(){
        return status;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public List<WarehouseOrderItem> getItems() {
        return items;
    }

    public void setItems(List<WarehouseOrderItem> items) {
        this.items = items;
    }

    public Warehouse getWarehouse() {
        return warehouse;
    }

    public void setWarehouse(Warehouse warehouse) {
        this.warehouse = warehouse;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
    }

    public List<WarehouseOrderRevision> getRevisions() {
        return revisions;
    }

    public void setRevisions(List<WarehouseOrderRevision> revisions) {
        this.revisions = revisions;
    }

    public boolean addRevision(WarehouseOrderRevision revision) {
        return revisions.add(revision);
    }

    public boolean addWarehouseOrderItem(WarehouseOrderItem warehouseOrderItem) {
        return items.add(warehouseOrderItem);
    }
    public boolean removeWarehouseOrderItem(WarehouseOrderItem warehouseOrderItem) {
        for (int i = 0; i < items.size(); i++) {
            if(items.get(i).getProduct().getId() == warehouseOrderItem.getProduct().getId()) {
                items.remove(i);
                return true;
            }
        }
        return items.remove(warehouseOrderItem);
    }

    public double calculateTotalPrice() {
        double price = 0;

        for (WarehouseOrderItem warehouseOrderItem: items) {
            price += warehouseOrderItem.getProduct().getPrice() * warehouseOrderItem.getQuantity();
        }

        return price;
    }

    @Override
    public String toString() {
        StringBuilder string = new StringBuilder("Order\n" +
                "Date: " + date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)) +
                "Warehouse: " + warehouse.toString() + "\n" +
                "Provider: " + provider.toString() + "\n" +
                "Items: \n");

        for (WarehouseOrderItem orderItem : items) {
            string.append("\t- ").append(orderItem.toString()).append("\n");
        }

        return string.toString();
    }
}

