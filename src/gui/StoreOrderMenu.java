package gui;

import controller.ControlException;
import controller.LoginController;
import controller.OrderController;
import database.DataAccessException;
import model.Order;
import model.Store;
import model.User;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class StoreOrderMenu extends JPanel 
{
	// Controllers
	private OrderController orderController;
	/**
	 * Create the panel.
	 */
	public StoreOrderMenu() {
		// Inicialize controllers
		try {
			orderController = new OrderController();
		} catch (DataAccessException e) {
			PopUp.newPopUp(this, e.getMessage(), "Error", PopUp.PopUpType.ERROR);
		}

		setLayout(new BorderLayout(0, 0));

		User user = LoginController.getLoggedInUser();

		if (user instanceof Store) {
			JPanel optionsPane = new JPanel();
			add(optionsPane, BorderLayout.NORTH);
			
			JButton btnCreateOrder = new JButton("Create Order");
			optionsPane.add(btnCreateOrder);
			btnCreateOrder.addActionListener(actionEvent -> createOrder());
		}
		
		JScrollPane scrollPane = new JScrollPane(ordersTable());
		add(scrollPane);
	}

	private void createOrder() {
		CreateOrderMenu component = new CreateOrderMenu();
		LayoutChangeMonitor.getInstance().setLayout(component, "create_order_menu");
	}

	private JTable ordersTable() {
		// Column names
		String[] columnNames = {"ID",
				"Date",
				"Store",
				"Revison",
				"Total Price (exc. VAT)"};

		// Create table with row edit disable
		try {
			// Convert data to 3d array
			List<Order> orders = orderController.getOrders();
			Object[][] alldata = new Object[orders.size()][];
			int i = 0;
			for (Order row : orders)
			{
				Object[] data = {
						row.getId(),
						row.getDate(),
						row.getStore(),
						row.getRevisions().size(),
						row.calculateTotalPrice()
				};

				alldata[i] = data;
				i++;
			}

			JTable table = new JTable(alldata, columnNames) {
				@Override
				public boolean editCellAt(int row, int column, java.util.EventObject e) {
					return false;
				}
			};
			// Disable selection
			table.setRowSelectionAllowed(false);
			// Set border
			table.setBorder(BorderFactory.createLineBorder(Color.blue));
			return table;

		} catch (ControlException e) {
			PopUp.newPopUp(this, e.getMessage(), "Error", PopUp.PopUpType.WARNING);
		}
		return null;
	}

}
