package gui;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

import controller.ControlException;
import controller.OrderController;
import database.DataAccessException;
import database.OrderDAO;
import database.OrderDB;
import gui.PopUp.PopUpType;
import model.Order;
import model.OrderItem;
import model.OrderRevision;
import model.Status;

public class OrderDeliveryConfirm extends JPanel {
	
	private final JList<OrderItem> orderItemList;
	private final JList<OrderItem> missingItemsList;
	private final JPanel itemControlPane;
    private JTextField orderItemAmount;

    private final int orderId;
    private Order order;
    private OrderRevision orderRevision;

	/**
	 * Create the panel.
	 */
	public OrderDeliveryConfirm(int orderId) {
		this.orderId = orderId;
		setLayout(new BorderLayout(0, 0));
		
		JPanel itemsPane = new JPanel();
		add(itemsPane, BorderLayout.CENTER);
		itemsPane.setLayout(new BorderLayout(0, 0));
		
		itemControlPane = new JPanel();
		itemsPane.add(itemControlPane, BorderLayout.NORTH);
		
		JSplitPane itemSplitPane = new JSplitPane();
		itemSplitPane.setResizeWeight(0.5);
		itemsPane.add(itemSplitPane, BorderLayout.CENTER);
		
		orderItemList = new JList<>();
		orderItemList.addListSelectionListener(listSelectionEvent -> {
			if (!listSelectionEvent.getValueIsAdjusting()) {
				EventQueue.invokeLater(() -> showControlsFor(orderItemList.getSelectedValue()));
			}
		});
		itemSplitPane.setLeftComponent(orderItemList);
		
		missingItemsList = new JList<>();
		itemSplitPane.setRightComponent(missingItemsList);
		
		JPanel buttonPane = new JPanel();
		add(buttonPane, BorderLayout.SOUTH);
		
		JButton btnConfirm = ColorStyle.newButton("Confirm");
		btnConfirm.addActionListener(actionEvent -> confirmDelivery());
		buttonPane.add(btnConfirm);
		
		JButton btnCancel = ColorStyle.newButton("Cancel");
		btnCancel.addActionListener(actionEvent -> quit());
		buttonPane.add(btnCancel);

		new Loader().start();
	}

	private void confirmDelivery() {
		try {
			order.setStatus(Status.DELIVERED);
			orderRevision.setStatus(Status.DELIVERED);
			orderRevision.setDate(LocalDateTime.now());
			orderRevision.setNote("Order delivered");

			order.addRevision(orderRevision);
			OrderController controller = new OrderController();
			controller.updateOrder(order);
			quit();
		} catch (DataAccessException | ControlException e) {
			PopUp.newPopUp(this, e.getMessage(), "Error", PopUpType.ERROR);
		}
	}

	private void quit() {
		EventQueue.invokeLater(() -> {
			OrderInformationMenu informationMenu = new OrderInformationMenu(orderId);
			LayoutChangeMonitor.getInstance().setLayout(informationMenu, "order_info_menu");
		});
	}

	private class Loader extends Thread {
		@Override
		public void run() {
			try {
				OrderDAO orderDAO = new OrderDB();
				order = orderDAO.selectByID(orderId);

				orderRevision = new OrderRevision(order);

				loadLists();
			} catch (DataAccessException e) {
				PopUp.newPopUp(getThis(), e.getMessage(), "Error loading order", PopUpType.ERROR);
			}
		}
	}

	private void loadLists() {
		if (order != null) {
			DefaultListModel<OrderItem> orderItemListModel = new DefaultListModel<>();
			List<OrderItem> dataList = order.getItems();
			for(OrderItem orderItem : dataList) {
				orderItemListModel.addElement(orderItem);
			}

			orderItemList.setModel(orderItemListModel);

			orderItemList.setCellRenderer(new ListCell());
		}
		if (orderRevision != null) {
			DefaultListModel<OrderItem> orderItemListModel = new DefaultListModel<>();
			List<OrderItem> dataList = orderRevision.getItemsChanged();
			for(OrderItem orderItem : dataList) {
				orderItemListModel.addElement(orderItem);
			}

			missingItemsList.setModel(orderItemListModel);

			missingItemsList.setCellRenderer(new ListCell());
		}
	}
	
	private void showControlsFor(OrderItem orderItem) {
    	itemControlPane.removeAll();

    	JLabel lblMissingItems = new JLabel("Amount of missing items: ");
    	itemControlPane.add(lblMissingItems);
        
        orderItemAmount = new JTextField("0");
        itemControlPane.add(orderItemAmount);
        orderItemAmount.setColumns(10);
        
        JButton btnConfirmAmount = ColorStyle.newButton("Confirm");
        btnConfirmAmount.addActionListener(event -> {
			try {
				int amount = Integer.parseInt(orderItemAmount.getText());
				OrderItem revisionItem = new OrderItem(
						orderItem.getProduct(),
						orderItem.getUnitPrice(),
						-amount
				);

				orderRevision.addItemChanged(revisionItem);
				order.setItemQuantity(orderItem.getProduct(), orderItem.getQuantity() - amount);

				EventQueue.invokeLater(this::loadLists);
			} catch (NumberFormatException e) {
				PopUp.newPopUp(this, e.getMessage(), "Warning", PopUpType.WARNING);
			}
        });
        itemControlPane.add(btnConfirmAmount);
        
        itemControlPane.revalidate();
        itemControlPane.repaint();
	}

	private OrderDeliveryConfirm getThis() {
		return this;
	}

}
