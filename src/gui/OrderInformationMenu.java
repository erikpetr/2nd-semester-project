package gui;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;

import controller.LoginController;
import controller.OrderController;
import database.DataAccessException;
import model.*;

import java.awt.*;
import java.time.format.DateTimeFormatter;
import java.util.List;

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
			PopUp.newPopUp(this, e.getMessage(), "Error loading order", PopUp.PopUpType.ERROR);
		}
		
		System.out.println("Showing order number " + order.getId());
		
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
		body.add(orderItems(order.getItems()));
		
		// Revisions
		body.add(orderRevisions());
		// --- /BODY ---*/

		User loggedInUser = LoginController.getLoggedInUser();

		boolean immutable = order.getStatus().equals(Status.REJECTED) || order.getStatus().equals(Status.DELIVERED);

		if (loggedInUser instanceof Warehouse && !immutable) {
			JPanel buttonPane = new JPanel();
			body.add(buttonPane);

			JButton btnUpdateStatus = new JButton("Update status");
			btnUpdateStatus.addActionListener(event -> {
				openUpdateWindow();
			});
			buttonPane.add(btnUpdateStatus);
		} else if (order.getStatus().equals(Status.IN_TRANSIT)) {
			JPanel buttonPane = new JPanel();
			body.add(buttonPane);

			JButton btnUpdateStatus = new JButton("Confirm delivery");
			btnUpdateStatus.addActionListener(event -> {
				EventQueue.invokeLater(() -> {
					OrderDeliveryConfirm deliveryConfirm = new OrderDeliveryConfirm(orderId);
					LayoutChangeMonitor.getInstance().setLayout(deliveryConfirm, "order_delivery_confirm");
				});
			});
			buttonPane.add(btnUpdateStatus);
		}
		
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
		return new JLabel("Order " + order.getId());
	}
	
	/*
	 * Creates panel for main details
	 */
	private Component orderDetails() 
	{
		// Details panel
		JPanel details = new JPanel();
		details.setLayout(new BoxLayout(details, BoxLayout.Y_AXIS));
		// labels
		JLabel date = new JLabel("Date: " + order.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - H:m")));
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
	private Component orderItems(List<OrderItem> orderItemList)
	{
		JPanel items = new JPanel();

		// Title
		JLabel title = new JLabel("Items");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		items.add(title);
		
		if (orderItemList.size() > 0)
		{			
			// Column names
			String[] columnNames = {
					"Product",
					"Quantity",
					"Unit Price"
					};
			
			// Convert items list
			Object[][] alldata = new Object[orderItemList.size()][];
			int i = 0;
			for (OrderItem row : orderItemList)
			{
				Object[] data = {
						row.getProduct().toString(),
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
	private JPanel orderRevisions()
	{
		// Panel
		JPanel revisions = new JPanel();
		revisions.setLayout(new BoxLayout(revisions, BoxLayout.Y_AXIS));
		
		JLabel title = new JLabel("Revisions");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		revisions.add(title);
		
		for(OrderRevision revision : order.getRevisions())
		{
			revisions.add(orderRevision(revision));
		}
		
		return revisions;
	}

	private JPanel orderRevision(OrderRevision orderRevision) {
		JPanel revision = new JPanel();
		revision.setLayout(new BoxLayout(revision, BoxLayout.Y_AXIS));

		JLabel title = new JLabel("Revision from date: "+orderRevision.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - H:m")));
		JLabel statusChange = new JLabel("Updated status to: "+orderRevision.getStatus().value);
		revision.add(title);
		revision.add(statusChange);

		if (!orderRevision.getItemsChanged().isEmpty()) {
			revision.add(orderItems(orderRevision.getItemsChanged()));
		}

		return revision;
	}
	
	private void openUpdateWindow() 
	{
		UpdateOrderMenu updateOrderMenu = new UpdateOrderMenu(order.getId());
		LayoutChangeMonitor.getInstance().setLayout(updateOrderMenu, "update_order_menu");
	}
}
