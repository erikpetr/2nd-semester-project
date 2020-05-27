package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.LinkedList;
import java.util.List;

public class OrderRevision {
    private int id;
    private LocalDateTime date;
    private Status status;
    private String note;
    private Order order;
    private List<OrderItem> itemsChanged;

    public OrderRevision(LocalDateTime date, Status status, String note, Order order, List<OrderItem> itemsChanged) {
        this.date = date;
        this.status = status;
        this.note = note;
        this.order = order;
        this.itemsChanged = itemsChanged;
    }

    public OrderRevision(int id, LocalDateTime date, Status status, String note, Order order, List<OrderItem> itemsChanged) {
        this.id = id;
        this.date = date;
        this.status = status;
        this.note = note;
        this.order = order;
        this.itemsChanged = itemsChanged;
    }
    
    public OrderRevision(Order order) {
    	this.order = order;
    	this.itemsChanged = new LinkedList<OrderItem>();
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

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public List<OrderItem> getItemsChanged() {
        return itemsChanged;
    }

    public void setItemsChanged(List<OrderItem> itemsChanged) {
        this.itemsChanged = itemsChanged;
    }

    public boolean addItemChanged(OrderItem orderItem) {
        for (int i = 0; i < itemsChanged.size(); i++) {
            if (itemsChanged.get(i).getProduct().getId() == orderItem.getProduct().getId()) {
                itemsChanged.set(i, orderItem);
                return true;
            }
        }
        return itemsChanged.add(orderItem);
    }

    public boolean changeProductQuantity(Product product, int quantity) {
        for (OrderItem item : itemsChanged) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(quantity);
                return true;
            }
        }
        return false;
    }

    public boolean hasOrderItem(OrderItem orderItem) {
        for (OrderItem item: itemsChanged) {
            if (orderItem.getProduct().getId() == item.getProduct().getId()) {
                return true;
            }
        }
        return false;
    }

    public boolean addQuantity(OrderItem orderItem, int quantity) {
        for (OrderItem item : itemsChanged) {
            if (item.getProduct().getId() == orderItem.getProduct().getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                return true;
            }
        }
        return false;
    }

    @Override
    public String toString() {
        return date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG)) + " - " + status.value + " (" + note + ")";
    }
}
