package gui;

import controller.ControlException;
import controller.LoginController;
import controller.OrderController;
import controller.WarehouseController;
import database.DataAccessException;
import gui.PopUp.PopUpType;
import model.Store;
import model.Warehouse;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class CreateOrderWarehouseChooser extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private DefaultListModel<Warehouse> warehouseListModel;
	private final JList<Warehouse> warehouseList;
	private final OrderController orderController;
	private final CreateOrderMenu parent;

	/**
	 * Create the dialog.
	 */
	public CreateOrderWarehouseChooser(CreateOrderMenu parent, OrderController orderController) {
		this.orderController = orderController;
		this.parent = parent;
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setLayout(new FlowLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		{
			warehouseList = new JList<>();
			contentPanel.add(warehouseList);
		}
		{
			JPanel buttonPane = new JPanel();
			buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
			getContentPane().add(buttonPane, BorderLayout.SOUTH);
			{
				JButton okButton = new JButton("OK");
				okButton.addActionListener(actionEvent -> ok());
				buttonPane.add(okButton);
				getRootPane().setDefaultButton(okButton);
			}
			{
				JButton cancelButton = new JButton("Cancel");
				cancelButton.addActionListener(actionEvent -> this.dispose());
				buttonPane.add(cancelButton);
			}
		}
		loadWarehouses();
	}

	/**
	 * Launch the application.
	 */
	public static void main(CreateOrderMenu parent, OrderController orderController) {
		try {
			CreateOrderWarehouseChooser dialog = new CreateOrderWarehouseChooser(parent, orderController);
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			PopUp.newPopUp(null, "There has been a catastrophic error.\nThe program will terminate\n" +
					e.getMessage(), "Failure", PopUp.PopUpType.ERROR);
			System.exit(-1);
		}
	}

	private void loadWarehouses() {
		try {
			warehouseListModel = new DefaultListModel<>();
			List<Warehouse> dataList;
			dataList = new WarehouseController().getWarehouses();

			for (Warehouse warehouse : dataList) {
				warehouseListModel.addElement(warehouse);
			}

			warehouseList.setModel(warehouseListModel);

			warehouseList.setCellRenderer(new ListCell());
		} catch (DataAccessException e) {
			PopUp.newPopUp(this, e.getMessage(), "Error", PopUpType.ERROR);
		}
	}
	
	private void ok() {
		if (warehouseList.getSelectedIndex() != -1) {
			Warehouse chosenWarehouse = warehouseList.getSelectedValue();
			Store store = (Store) LoginController.getLoggedInUser();
			
			try {
				if (orderController.getOrder() != null) {
					if (orderController.getOrder().getWarehouse().getId() != chosenWarehouse.getId()) {
						orderController.createOrder(store, chosenWarehouse);
					}
				} else {
					orderController.createOrder(store, chosenWarehouse);
				}
			} catch (ControlException e) {
				PopUp.newPopUp(this, e.getMessage(), "Error", PopUpType.ERROR);
			}
			parent.init();
			this.dispose();
		} else {
			PopUp.newPopUp(this, "Please choose a Warehouse", "Can't continue", PopUpType.WARNING);
		}
		
	}

}
