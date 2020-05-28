package model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.LinkedList;
import java.util.List;

public class Order {

    private int id;
    private Store store;
    private Warehouse warehouse;
    private LocalDateTime date;
    private Status status;
    // Items in order
    private List<OrderItem> items;
    // Revisions for order
    private List<OrderRevision> revisions;

    public Order(Store store, Warehouse warehouse) {
        this.store = store;
        this.warehouse = warehouse;
        items = new LinkedList<>();
        revisions = new LinkedList<>();
    }

    public Order(int id, LocalDateTime date, Status status, List<OrderRevision> revisions, Warehouse warehouse, Store store){
        this.id = id;
        this.date = date;
        this.status = status;
        this.revisions = revisions;
        this.warehouse = warehouse;
        this.store = store;
        items = new LinkedList<>();
    }
    
    public Order(int id, LocalDateTime date, Status status, Warehouse warehouse, Store store, List<OrderItem> items, List<OrderRevision> revisions) {
		super();
		this.id = id;
		this.date = date;
		this.status = status;
		this.warehouse = warehouse;
		this.store = store;
		this.items = items;
		this.revisions = revisions;
	}
    
    // Create object to list in table
    public Order(int id, Store store, Warehouse warehouse, LocalDateTime date, Status status)
    {
    	this.id = id;
    	this.store = store;
    	this.warehouse = warehouse;
    	this.status = status;
    	this.date = date;
    	
    	// Inicialize lists
    	this.items = new LinkedList<OrderItem>();
    	this.revisions = new LinkedList<OrderRevision>();
    }
    
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public Warehouse getWarehouse() {
		return warehouse;
	}

	public void setWarehouse(Warehouse warehouse) {
		this.warehouse = warehouse;
	}

	public Store getStore() {
		return store;
	}

	public void setStore(Store store) {
		this.store = store;
	}

	public List<OrderItem> getItems() {
		return items;
	}

	public boolean addOrderItem(OrderItem orderItem) {
		return items.add(orderItem);
	}

	public boolean removeOrderItem(OrderItem orderItem) {
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i).getProduct().getId() == orderItem.getProduct().getId()) {
				items.remove(i);
				return true;
			}
		}
		return items.remove(orderItem);
	}

	public void setItems(List<OrderItem> orderItems) {
		this.items = orderItems;
	}

	public boolean setItemQuantity(Product product, int quantity) {
    	for (OrderItem orderItem : items) {
    		if (product.getId() == orderItem.getProduct().getId()) {
    			orderItem.setQuantity(quantity);
    			return true;
			}
		}
    	return false;
	}

	public List<OrderRevision> getRevisions() {
		return revisions;
	}

	public void setRevisions(List<OrderRevision> revisions) {
		this.revisions = revisions;
	}

	public boolean addRevision(OrderRevision revision) {
    	return revisions.add(revision);
	}

	public boolean setQuantity(OrderItem orderItem, int quantity) {
    	for (OrderItem item : items) {
    		if (item.getProduct().getId() == orderItem.getProduct().getId()) {
    			item.setQuantity(quantity);
    			return true;
			}
		}
    	return false;
	}

	public double calculateTotalPrice() {
    	double price = 0;

    	for (OrderItem orderItem: items) {
    		price += orderItem.getProduct().getPrice() * orderItem.getQuantity();
		}

    	return price;
	}

	public double calculateTotalWeight() {
    	double weight = 0;

		for (OrderItem orderItem: items) {
			weight += orderItem.getProduct().getWeight() * orderItem.getQuantity();
		}

		return weight;
	}

	@Override
	public String toString() {
		StringBuilder string = new StringBuilder("Order\n");
		if (date != null) {
			string.append("Date: ").append(date.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG))).append("\n");
		}
		string.append("Warehouse: ").append(warehouse.toString()).append("\n");
		string.append("Store: ").append(store.toString()).append("\n");
		string.append("Items: \n");

		for (OrderItem orderItem : items) {
			string.append("\t- ").append(orderItem.toString()).append("\n");
		}

		string.append("\nTotal Price: ").append(calculateTotalPrice()).append(" EUR");

		return string.toString();
	}
}


