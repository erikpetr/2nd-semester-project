package gui;

import controller.LoginController;
import database.*;
import model.User;
import model.Warehouse;

import javax.swing.*;
import java.awt.*;

public class WarehouseDashboard extends JPanel {

    private JLabel lblPendingOrders;
    private JLabel lblTotalOrders;
    private JLabel lblTotalStocks;
    private JLabel lblLowStocks;

    /**
     * Create the panel.
     */
    public WarehouseDashboard() {
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

        JPanel stockInfoPane = new JPanel();
        add(stockInfoPane);
        stockInfoPane.setLayout(new BoxLayout(stockInfoPane, BoxLayout.Y_AXIS));

        JLabel lblStockInfo = new JLabel("Stock");
        stockInfoPane.add(lblStockInfo);

        lblLowStocks = new JLabel("");
        stockInfoPane.add(lblLowStocks);

        lblTotalStocks = new JLabel("");
        stockInfoPane.add(lblTotalStocks);

        new InfoLoader().run();
    }

    private class InfoLoader implements Runnable {

        @Override
        public void run() {
            try {
                OrderDAO orderDAO = new OrderDB();
                StockDAO stockDAO = new StockDB();

                User user = LoginController.getLoggedInUser();
                int pendingOrders = orderDAO.getPendingOrdersAmount(user);
                int totalOrders = orderDAO.getOrdersAmount(user);
                int lowStocks = stockDAO.getStocksBelowMinQuantityAmount((Warehouse) user);
                int totalStocks = stockDAO.getStocksAmount((Warehouse) user);

                EventQueue.invokeLater(() -> {
                    lblPendingOrders.setText("Pending Orders: " + pendingOrders);
                    lblTotalOrders.setText("Total Orders: " + totalOrders);
                    lblLowStocks.setText("Stocks low: " + lowStocks);
                    lblTotalStocks.setText("Total Stocks: " + totalStocks);
                });
            } catch (DataAccessException e) {
                PopUp.newPopUp(WarehouseDashboard.this, e.getMessage(), "Error", PopUp.PopUpType.ERROR);
            }
        }
    }
}
