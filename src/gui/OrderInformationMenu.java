package gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import controller.LoginController;
import controller.OrderController;
import database.DataAccessException;
import model.Order;
import model.OrderItem;
import model.OrderRevision;
import model.Store;
import model.Warehouse;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;

public class OrderInformationMenu extends JPanel {
	
	private Order order;
	
	// Panels
	private JPanel panel;
	
	public OrderInformationMenu(int orderId) 
	{
		//Get order
		try {
			order = new OrderController().getOrder(orderId);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Shwoing order number " + order.getId());
		
		// Main panel
		panel = new JPanel();
		//panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setLayout(new BorderLayout());
		
		// --- HEADER ---
		JPanel header = new JPanel();
		FlowLayout flowLayout = (FlowLayout) header.getLayout();
		flowLayout.setAlignOnBaseline(true);
		flowLayout.setVgap(10);
		flowLayout.setHgap(10);
		
		// Set border
		//header.setBorder(BorderFactory.createLineBorder(Color.black));
		
		// title
		header.add(titlePanel());
		// Add header to main panel
		panel.add(header, BorderLayout.NORTH);
		// --- /HEADER ---
		
		// --- BODY ---
		// Scroll Pane
		JScrollPane scrollPane = new JScrollPane();
		//scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		panel.add(scrollPane, BorderLayout.CENTER);
		
		// Body panel
		JPanel body = new JPanel();
		body.setLayout(new GridLayout(0,1));
		// Add body panel to scroll panel
		scrollPane.setViewportView(body);
		
		// Order details
		body.add(orderDetails());
		
		// Order items
		body.add(orderItems());
		
		// Revisions
		body.add(orderRevisions());
		// --- /BODY ---*/
		
		add(panel);
		// Set visible
		setVisible(true);
	}
	
	public OrderInformationMenu()
	{
		JLabel noresults = new JLabel("No order selected.");
		add(noresults);
		
		setVisible(true);
	}
	
	/*
	 * Title panel for window
	 */
	private Component titlePanel() 
	{
		// Title label
		JLabel title = new JLabel("ORDER DESCRIPTION (ORDER ID : " + order.getId() + ")");
		return title;
	}
	
	/*
	 * Creates panel for main details
	 */
	private Component orderDetails() 
	{
		// Details panel
		JPanel details = new JPanel();
		FlowLayout fl_details = new FlowLayout(FlowLayout.CENTER, 50, 30);
		details.setLayout(fl_details);
		// labels
		JLabel date = new JLabel("Date: " + order.getDate());
		details.add(date);
		if (LoginController.getLoggedInUser() instanceof Warehouse)
		{
			JLabel store = new JLabel("Store: " + order.getStore());
			details.add(store);
		}
		else if (LoginController.getLoggedInUser() instanceof Store)
		{
			JLabel warehouse = new JLabel("Warehouse: " + order.getWarehouse());
			details.add(warehouse);
		}
		
		
		JLabel price = new JLabel("Total Price: " + order.calculateTotalPrice());
		details.add(price);
		
		return details;
	}
	
	/*
	 * Creates scroll panel for order items
	 */
	private Component orderItems()
	{
		JPanel items = new JPanel();

		// Title
		JLabel title = new JLabel("Items");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		items.add(title);
		
		if (order.getItems().size() > 0)
		{			
			// Column names
			String[] columnNames = {
					"Product",
					"Quantity",
					"Unit Price"
					};
			
			// Convert items list
			Object[][] alldata = new Object[order.getItems().size()][];
			int i = 0;
			for (OrderItem row : order.getItems())
			{
				Object[] data = {
						row.getProduct(),
						row.getQuantity(),
						row.getUnitPrice()
				};

				alldata[i] = data;
				i++;
			}

			// Items table
			JTable table = new JTable(alldata, columnNames) {
				@Override
				public boolean editCellAt(int row, int column, java.util.EventObject e) {
					return false;
				}
			};
			
			// Remove row selection
			table.setRowSelectionAllowed(false);
			
			// Add table to scroll pane
			items.add(table);
		}
		else
		{
			JLabel noresults = new JLabel("No items on this order.");
			items.add(noresults);
		}
		
		return items;
	}
	
	/*
	 * Creates panel for revisions
	 */
	private Component orderRevisions()
	{
		// Panel
		JPanel revisions = new JPanel();
		revisions.setLayout(new BoxLayout(revisions, BoxLayout.Y_AXIS));
		
		JLabel title = new JLabel("Revisions");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		revisions.add(title);
		
		for(OrderRevision revision : order.getRevisions())
		{
			JLabel single_revision = new JLabel(revision.toString());
			single_revision.add(revisions);
		}
		
		
		if (LoginController.getLoggedInUser() instanceof Warehouse) 
		{
			JPanel buttonPane = new JPanel();
			revisions.add(buttonPane);
			
			JButton btnUpdateStatus = new JButton("Update status");
			btnUpdateStatus.addActionListener(event -> {
				openUpdateWindow();
			});
			buttonPane.add(btnUpdateStatus);
		}
		
		return revisions;
	}
	
	private void openUpdateWindow() 
	{
		UpdateOrderMenu updateOrderMenu = new UpdateOrderMenu(order.getId());
		LayoutChangeMonitor.getInstance().setLayout(updateOrderMenu, "update_order_menu");
	}
}
