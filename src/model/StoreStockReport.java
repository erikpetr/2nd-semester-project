package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedList;
import java.util.List;

public class StoreStockReport {

    private int id;
    private LocalDateTime date;
    private String note;
    private Store store;
    private List<StoreStockReportItem> items;

    public StoreStockReport(int id, LocalDateTime date, String note, Store store, List<StoreStockReportItem> items) {
        this.id = id;
        this.date = date;
        this.note = note;
        this.store = store;
        this.items = items;
    }

    public StoreStockReport(LocalDateTime date, String note, Store store, List<StoreStockReportItem> items) {
        this.date = date;
        this.note = note;
        this.store = store;
        this.items = items;
    }

    public StoreStockReport(Store store) {
        this.date = null;
        this.note = "";
        this.store = store;
        this.items = new LinkedList<>();
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public boolean addItem(StoreStockReportItem item) {
        return items.add(item);
    }

    public List<StoreStockReportItem> getItems() {
        return items;
    }

    public void setItems(List<StoreStockReportItem> items) {
        this.items = items;
    }

    public String toString() {
        return String.valueOf(this.getId()) + " : " + this.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) + " , " + this.getNote();
    }

    public boolean removeReportItem(StoreStockReportItem reportItem) {
        for (StoreStockReportItem item : items) {
            if (item.getProduct().getId() == reportItem.getProduct().getId()) {
                items.remove(item);
                return true;
            }
        }
        return items.remove(reportItem);
    }

    public double calculateTotalPrice() {
        double price = 0;

        for (StoreStockReportItem reportItem : items) {
            price += reportItem.getProduct().getPrice() * reportItem.getQuantity();
        }

        return price;
    }
}

