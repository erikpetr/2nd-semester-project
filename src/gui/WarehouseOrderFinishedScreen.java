package gui;

import controller.WarehouseOrderController;
import database.DataAccessException;
import model.WarehouseOrder;
import model.WarehouseOrderItem;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class WarehouseOrderFinishedScreen extends JPanel {
    private final int warehouseOrderId;
    private WarehouseOrder warehouseOrder;
    private final JList<WarehouseOrderItem> warehouseOrderItemList;

    /**
     * Create the panel.
     */
    public WarehouseOrderFinishedScreen(int id) {
        this.warehouseOrderId = id;

        setLayout(new BorderLayout(0, 0));

        JPanel infoPanel = new JPanel();
        add(infoPanel, BorderLayout.NORTH);

        JLabel lblOrderInfo = new JLabel("Warehouse order " +warehouseOrderId+ " finished");
        lblOrderInfo.setFont(new Font("Dialog", Font.BOLD, 20));
        infoPanel.add(lblOrderInfo);

        JPanel orderItemsPanel = new JPanel();
        add(orderItemsPanel, BorderLayout.CENTER);

        warehouseOrderItemList = new JList<>();
        orderItemsPanel.add(warehouseOrderItemList);

        JPanel footerPanel = new JPanel();
        add(footerPanel, BorderLayout.SOUTH);

        JLabel lblTotalPrice = new JLabel("Total price "+warehouseOrder.calculateTotalPrice()+" €");
        footerPanel.add(lblTotalPrice);

        EventQueue.invokeLater(this::loadWarehouseOrder);
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
            DefaultListModel<WarehouseOrderItem> warehouseOrderItemListModel = new DefaultListModel<>();
            List<WarehouseOrderItem> dataList = warehouseOrder.getItems();
            for(WarehouseOrderItem orderItem : dataList) {
                warehouseOrderItemListModel.addElement(orderItem);
            }

            warehouseOrderItemList.setModel(warehouseOrderItemListModel);

            warehouseOrderItemList.setCellRenderer(new ListCell());
        }
    }
}
