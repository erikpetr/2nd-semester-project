package gui;

import controller.WarehouseOrderController;
import database.DataAccessException;
import model.WarehouseOrder;
import model.WarehouseOrderItem;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WarehouseOrderFinishedScreen extends JPanel {
    private int warehouseOrderId;
    private WarehouseOrder warehouseOrder;
    private JList<WarehouseOrderItem> warehouseOrderItemList;
    private DefaultListModel<WarehouseOrderItem> warehouseOrderItemListModel;

    /**
     * Create the panel.
     */
    public WarehouseOrderFinishedScreen(int id) {
        this.warehouseOrderId = id;
        loadWarehouseOrder();

        setLayout(new BorderLayout(0, 0));

        JPanel infoPanel = new JPanel();
        add(infoPanel, BorderLayout.NORTH);

        JLabel lblOrderInfo = new JLabel("Warehouse order " +warehouseOrderId+ " finished");
        lblOrderInfo.setFont(new Font("Dialog", Font.BOLD, 20));
        infoPanel.add(lblOrderInfo);

        JPanel orderItemsPanel = new JPanel();
        add(orderItemsPanel, BorderLayout.CENTER);

        warehouseOrderItemList = new JList<WarehouseOrderItem>();
        orderItemsPanel.add(warehouseOrderItemList);

        JPanel footerPanel = new JPanel();
        add(footerPanel, BorderLayout.SOUTH);

        JLabel lblTotalPrice = new JLabel("Total price "+warehouseOrder.calculateTotalPrice()+" €");
        footerPanel.add(lblTotalPrice);
    }

    public void loadWarehouseOrder() {
        try {
            WarehouseOrderController warehouseOrderController = new WarehouseOrderController();
            warehouseOrder = warehouseOrderController.getWarehouseOrder(warehouseOrderId);
        } catch (DataAccessException e) {
            PopUp.newPopUp(this, e.getMessage(), "Error loading warehouse order", PopUp.PopUpType.ERROR);
        }
    }

    private void loadWarehuseOrderItems() {
        if (warehouseOrder != null) {
            warehouseOrderItemListModel = new DefaultListModel<>();
            List<WarehouseOrderItem> dataList = warehouseOrder.getItems();
            for(WarehouseOrderItem orderItem : dataList) {
                warehouseOrderItemListModel.addElement(orderItem);
            }

            warehouseOrderItemList.setModel(warehouseOrderItemListModel);

            warehouseOrderItemList.setCellRenderer(new ListCell());
        }
    }
}