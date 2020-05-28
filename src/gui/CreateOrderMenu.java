package gui;

import controller.ControlException;
import controller.OrderController;
import controller.ProductController;
import database.DataAccessException;
import gui.PopUp.PopUpType;
import model.Order;
import model.OrderItem;
import model.Product;
import model.Warehouse;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CreateOrderMenu extends JPanel {

    private OrderController orderController;
    private ProductController productController;

    private DefaultListModel<Product> productListModel;
    private DefaultListModel<OrderItem> orderItemListModel;
    private final JList<Product> productList;
    private final JList<OrderItem> orderItemList;
    private JLabel lblOrderStatus;
    private JPanel optionsPanel;
    private JTextField orderItemAmount;
    private JTextField productAmount;
    
    /**
     * Create the panel.
	 * TODO: Add weight limit, for popup
     */
    public CreateOrderMenu() {
        try {
			orderController = new OrderController();
			productController = new ProductController();
		} catch (DataAccessException e) {
			PopUp.newPopUp(this, e.getMessage(), "Error", PopUpType.ERROR);
		}
        setLayout(new BorderLayout(0, 0));
        
        optionsPanel = new JPanel();
        add(optionsPanel, BorderLayout.NORTH);
        
        JSplitPane productPane = new JSplitPane();
        productPane.setResizeWeight(0.5);
        productPane.setContinuousLayout(true);
        add(productPane, BorderLayout.CENTER);

        productList = new JList<>();
        productList.addListSelectionListener(e -> {
        	if (!e.getValueIsAdjusting()) {
            	showControlsFor(productList.getSelectedValue());
        	}
        });
        productPane.setLeftComponent(productList);

        orderItemList = new JList<>();
        orderItemList.addListSelectionListener(e -> {
        	if (orderItemList.getSelectedValue() != null) {
        		showControlsFor(orderItemList.getSelectedValue());
        	}
        });
        productPane.setRightComponent(orderItemList);

        JPanel sidePanel = new JPanel();
        add(sidePanel, BorderLayout.EAST);
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));
        
        JButton btnChooseWarehouse = ColorStyle.newButton("Choose warehouse");
		btnChooseWarehouse.addActionListener(button -> {
			chooseWarehouse();
		});
        
        lblOrderStatus = new JLabel("Choose a warehouse");
        sidePanel.add(lblOrderStatus);
        sidePanel.add(btnChooseWarehouse);

        JButton btnCancelOrder = ColorStyle.newCancelButton("Cancel");
        btnCancelOrder.addActionListener(actionEvent -> cancelOrder());
        sidePanel.add(btnCancelOrder);

        JButton btnSubmitOrder = ColorStyle.newButton("Submit");
		btnSubmitOrder.addActionListener(button -> createOrder());
		sidePanel.add(btnSubmitOrder);
	}
    
    public void init() {
    	updateOrderStatus();
    	loadOrderItems();
    	loadProducts();
    	resetOptionsPanel();
    }

    public void updateOrderStatus() {
		if (orderController.getOrder() != null) {
			lblOrderStatus.setText("Total price: "+orderController.getOrder().calculateTotalPrice());
		} else {
			lblOrderStatus.setText("Choose a warehouse");
		}
	}
    
    private void showControlsFor(Product product) {
    	optionsPanel.removeAll();
    	
    	JLabel lblAmountLabel = new JLabel("Amount:");
        optionsPanel.add(lblAmountLabel);
        
        productAmount = new JTextField();
        optionsPanel.add(productAmount);
        productAmount.setColumns(10);
        productAmount.setText("1");
        
        JButton btnAddProduct = ColorStyle.newButton("Add product");
        btnAddProduct.addActionListener(event -> {
        	int amount = Integer.parseInt(productAmount.getText());
        	try {
				orderController.addProduct(product, amount);
			} catch (ControlException e) {
				PopUp.newPopUp(this, e.getMessage(), "Warning", PopUpType.WARNING);
			}
        	init();
        });
        optionsPanel.add(btnAddProduct);
        
        optionsPanel.revalidate();
        optionsPanel.repaint();
    }
    
    private void showControlsFor(OrderItem orderItem) {
    	int startQuantity = orderItem.getQuantity();
    	optionsPanel.removeAll();
    	
    	JButton btnRemoveItem = ColorStyle.newCancelButton("Remove order item");
    	btnRemoveItem.addActionListener(e -> {
    		orderController.removeProduct(orderItem.getProduct());
    		PopUp.newPopUp(this, "Order updated", "Success", PopUpType.INFORMATION);
    		init();
    	});
        optionsPanel.add(btnRemoveItem);
        
        JButton btnDecreaseAmount = ColorStyle.newButton("<< Decrement");
        btnDecreaseAmount.addActionListener(e -> {
    		int quantity = Integer.parseInt(orderItemAmount.getText());
    		quantity--;
    		orderItemAmount.setText(quantity+"");
    	});
        optionsPanel.add(btnDecreaseAmount);
        
        orderItemAmount = new JTextField();
        optionsPanel.add(orderItemAmount);
        orderItemAmount.setColumns(10);
        orderItemAmount.setText(orderItem.getQuantity()+"");
        
        JButton btnIncreaseAmount = ColorStyle.newButton("Increment >>");
        btnIncreaseAmount.addActionListener(e -> {
    		int quantity = Integer.parseInt(orderItemAmount.getText());
    		quantity++;
    		orderItemAmount.setText(quantity+"");
    	});
        optionsPanel.add(btnIncreaseAmount);
        
        JButton btnConfirmAmount = ColorStyle.newButton("Confirm");
        btnConfirmAmount.addActionListener(event -> {
        	try {
        		int quantity = Integer.parseInt(orderItemAmount.getText());
            	if (startQuantity < quantity) {
    				orderController.addProduct(orderItem.getProduct(), quantity - startQuantity);
    			} else {
    				orderController.removeProduct(orderItem.getProduct(), startQuantity - quantity);
    			}
            	PopUp.newPopUp(this, "Order updated", "Success", PopUpType.INFORMATION);
        	} catch (ControlException e) {
				PopUp.newPopUp(this, e.getMessage(), "Warning", PopUpType.WARNING);
			}
        	init();
        });
        optionsPanel.add(btnConfirmAmount);
        
        optionsPanel.revalidate();
        optionsPanel.repaint();
    }
    
    private void resetOptionsPanel() {
    	optionsPanel.removeAll();
    	optionsPanel.revalidate();
    	optionsPanel.repaint();
    }
    
    private void chooseWarehouse() {
    	CreateOrderWarehouseChooser.main(this, orderController);
    }
	
	private void loadOrderItems() {
		if (orderController.getOrder() != null) {
			orderItemListModel = new DefaultListModel<>();
			List<OrderItem> dataList = orderController.getOrder().getItems();
			for(OrderItem orderItem : dataList) {
                orderItemListModel.addElement(orderItem);
			}
			
			orderItemList.setModel(orderItemListModel);
			
			orderItemList.setCellRenderer(new ListCell());
		}
	}
	
	private void loadProducts() {
		new ProductLoader(this).start();
	}

	private void cancelOrder() {
    	orderController.setOrder(null);
    	this.setVisible(false);
	}
	
	private void createOrder() {
		try {
			int orderId = orderController.finishOrder();
			LayoutChangeMonitor.getInstance().setLayout(new OrderFinishedScreen(orderId), "order_finished");
		} catch (ControlException e) {
			PopUp.newPopUp(this, e.getMessage(), "Can't finish order", PopUpType.WARNING);
		}
		
	}
	
	private class ProductLoader extends Thread {
		JComponent parent;
		public ProductLoader(JComponent parent) {
			this.parent = parent;
		}
		@Override
		public void run() {
			if (orderController.getOrder() != null) {
				try {
					productListModel = new DefaultListModel<>();
					Warehouse warehouse = orderController.getOrder().getWarehouse();
					List<Product> dataList = productController.getProducts(warehouse);
					for(Product product : dataList) {
	                    productListModel.addElement(product);
					}
					
					productList.setModel(productListModel);
					
					productList.setCellRenderer(new ListCell());
				} catch (DataAccessException e) {
					PopUp.newPopUp(parent, e.getMessage(), "Error", PopUpType.ERROR);
				}
			}
		}
	}
}
