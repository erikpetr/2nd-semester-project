package gui;

import controller.OrderController;
import database.DataAccessException;
import model.Order;
import model.OrderItem;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class OrderFinishedScreen extends JPanel {
    private final int orderId;
    private final JList<OrderItem> orderItemList;
    private Order order;

    /**
     * Create the panel.
     */
    public OrderFinishedScreen(int id) {
        this.orderId = id;
        loadOrder();

        setLayout(new BorderLayout(0, 0));

        JPanel infoPanel = new JPanel();
        add(infoPanel, BorderLayout.NORTH);

        JLabel lblOrderInfo = new JLabel("Order " + orderId + " finished");
        lblOrderInfo.setFont(new Font("Dialog", Font.BOLD, 20));
        infoPanel.add(lblOrderInfo);

        JPanel orderItemsPanel = new JPanel();
        add(orderItemsPanel, BorderLayout.CENTER);

        orderItemList = new JList<>();
        orderItemsPanel.add(orderItemList);

        JPanel footerPanel = new JPanel();
        add(footerPanel, BorderLayout.SOUTH);

        JLabel lblTotalPrice = new JLabel("Total price " + order.calculateTotalPrice() + " €");
        footerPanel.add(lblTotalPrice);
    }

    public void loadOrder() {
        try {
            OrderController orderController = new OrderController();
            order = orderController.getOrder(orderId);
        } catch (DataAccessException e) {
            PopUp.newPopUp(this, e.getMessage(), "Error loading order", PopUp.PopUpType.ERROR);
        }
    }

    private void loadOrderItems() {
        if (order != null) {
            DefaultListModel<OrderItem> orderItemListModel = new DefaultListModel<>();
            List<OrderItem> dataList = order.getItems();
            for (OrderItem orderItem : dataList) {
                orderItemListModel.addElement(orderItem);
            }

            orderItemList.setModel(orderItemListModel);

            orderItemList.setCellRenderer(new ListCell());
        }
    }

}
