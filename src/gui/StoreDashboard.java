package gui;

import controller.LoginController;
import database.DataAccessException;
import database.OrderDAO;
import database.OrderDB;
import model.User;

import javax.swing.*;
import java.awt.*;

public class StoreDashboard extends JPanel {

    private JLabel lblPendingOrders;
    private JLabel lblTotalOrders;

    /**
     * Create the panel.
     */
    public StoreDashboard() {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        JPanel ordersInfoPane = new JPanel();
        add(ordersInfoPane);
        ordersInfoPane.setLayout(new BoxLayout(ordersInfoPane, BoxLayout.Y_AXIS));

        JLabel lblOrderPane = new JLabel("Orders");
        ordersInfoPane.add(lblOrderPane);

        lblPendingOrders = new JLabel("");
        ordersInfoPane.add(lblPendingOrders);

        lblTotalOrders = new JLabel("");
        ordersInfoPane.add(lblTotalOrders);

        new InfoLoader().run();
    }

    private class InfoLoader implements Runnable {

        @Override
        public void run() {
            try {
                OrderDAO orderDAO = new OrderDB();

                User user = LoginController.getLoggedInUser();
                int pendingOrders = orderDAO.getPendingOrdersAmount(user);
                int totalOrders = orderDAO.getOrdersAmount(user);

                EventQueue.invokeLater(() -> {
                    lblPendingOrders.setText("Pending Orders: " + pendingOrders);
                    lblTotalOrders.setText("Total Orders: " + totalOrders);
                });
            } catch (DataAccessException e) {
                PopUp.newPopUp(StoreDashboard.this, e.getMessage(), "Error", PopUp.PopUpType.ERROR);
            }
        }
    }


}
