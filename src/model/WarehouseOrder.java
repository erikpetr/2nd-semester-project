package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class WarehouseOrder {

    private int id;
    private LocalDate date;
    private Status status;
    private Warehouse warehouse;
    private Provider provider;
    private List<WarehouseOrderItem> items;

    public WarehouseOrder(int id, LocalDate date, Status status, Warehouse warehouse, Provider provider, List<WarehouseOrderItem> items)
    {
        this.id = id;
        this.date = date;
        this.status = status;
        this.warehouse = warehouse;
        this.provider = provider;
        this.items = items;
    }

    public WarehouseOrder(LocalDate date, Status status, Warehouse warehouse, Provider provider, List<WarehouseOrderItem> items) {
        this.date = date;
        this.status = status;
        this.warehouse = warehouse;
        this.provider = provider;
        this.items = items;
    }

    public int getId(){
        return id;
    }

    public LocalDate getDate(){
        return date;
    }

    public Status getStatus(){
        return status;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void addItem(WarehouseOrderItem item) {
        items.add(item);
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

