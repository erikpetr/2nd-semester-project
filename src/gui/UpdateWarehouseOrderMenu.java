package gui;

import controller.ControlException;
import controller.WarehouseOrderController;
import database.DataAccessException;
import model.Status;
import model.WarehouseOrder;
import model.WarehouseOrderItem;
import model.WarehouseOrderRevision;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class UpdateWarehouseOrderMenu extends JPanel {
    private final JPanel optionsPanel;
    private final JTextArea textRevisionNote;
    private final JList<WarehouseOrderItem> warehouseOrderItemList;
    private final JComboBox<String> statusBox;
    private JTextField orderItemAmount;
    private WarehouseOrderController controller;
    private WarehouseOrder order;
    private WarehouseOrderRevision revision;

    /**
     * Create the panel.
     */
    public UpdateWarehouseOrderMenu(int orderId) {

        try {
            controller = new WarehouseOrderController();
            order = controller.getWarehouseOrder(orderId);
            revision = new WarehouseOrderRevision(order);
        } catch (DataAccessException e) {
            PopUp.newPopUp(this, e.getMessage(), "Connection failed", PopUp.PopUpType.ERROR);
        }

        setLayout(new BorderLayout(0, 0));

        optionsPanel = new JPanel();
        add(optionsPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        warehouseOrderItemList = new JList<>();
        warehouseOrderItemList.addListSelectionListener(listSelectionEvent -> {
            if (!listSelectionEvent.getValueIsAdjusting() && warehouseOrderItemList.getSelectedValue() != null) {
                showControlsFor(warehouseOrderItemList.getSelectedValue());
            }
        });
        splitPane.setLeftComponent(warehouseOrderItemList);

        JPanel controlPanel = new JPanel();
        splitPane.setRightComponent(controlPanel);
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        JLabel lblOrderId = new JLabel("Warehouse order " + orderId);
        controlPanel.add(lblOrderId);

        textRevisionNote = new JTextArea();
        textRevisionNote.setToolTipText("Add a note");
        controlPanel.add(textRevisionNote);

        DefaultComboBoxModel<String> comboModel = new DefaultComboBoxModel<>(loadStatuses());
        statusBox = new JComboBox<>(comboModel);
        statusBox.setAlignmentX(0.0f);
        statusBox.setSelectedItem(order.getStatus().value);
        controlPanel.add(statusBox);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setAlignmentX(0.0f);
        controlPanel.add(buttonPanel);

        JButton btnSubmitButton = ColorStyle.newButton("Submit");
        btnSubmitButton.addActionListener(actionEvent -> submit());
        buttonPanel.add(btnSubmitButton);

        JButton btnCancelButton = ColorStyle.newButton("Cancel");
        btnCancelButton.addActionListener(actionEvent -> {
            WarehouseOrderInformationMenu informationMenu = new WarehouseOrderInformationMenu(orderId);
            LayoutChangeMonitor.getInstance().setLayout(informationMenu, "warehouse_order_info");
        });
        buttonPanel.add(btnCancelButton);

        init();
    }

    public void init() {
        loadOrderItems();
    }

    public void submit() {
        try {
            Status status = Status.get(statusBox.getSelectedItem().toString());
            revision.setNote(textRevisionNote.getText());
            revision.setStatus(status);
            revision.setDate(LocalDateTime.now());
            order.addRevision(revision);
            order.setStatus(status);
            controller.updateWarehouseOrder(order);
            WarehouseOrderInformationMenu informationMenu = new WarehouseOrderInformationMenu(order.getId());
            LayoutChangeMonitor.getInstance().setLayout(informationMenu, "warehouse_order_info");
        } catch (ControlException e) {
            PopUp.newPopUp(this, e.getMessage(), "Can't update warehouse order", PopUp.PopUpType.ERROR);
        }
    }

    private void showControlsFor(WarehouseOrderItem orderItem) {
        int startQuantity = orderItem.getQuantity();
        optionsPanel.removeAll();

        JButton btnRemoveItem = ColorStyle.newButton("RemoveWarehouseOrderItem");
        btnRemoveItem.addActionListener(e -> {
            order.removeWarehouseOrderItem(orderItem);
            PopUp.newPopUp(this, "Warehouse order updated", "Success", PopUp.PopUpType.INFORMATION);
            init();
        });
        optionsPanel.add(btnRemoveItem);

        JButton btnDecreaseAmount = ColorStyle.newButton("<< Decrement");
        btnDecreaseAmount.addActionListener(event -> {
            try {
                int quantity = Integer.parseInt(orderItemAmount.getText());
                if (quantity > 0) {
                    quantity--;
                    orderItemAmount.setText(quantity + "");
                }
            } catch (NumberFormatException e) {
                PopUp.newPopUp(this, "Make sure the value is numeric", "Cannot continue", PopUp.PopUpType.WARNING);
            }
        });
        optionsPanel.add(btnDecreaseAmount);

        orderItemAmount = new JTextField(startQuantity + "");
        optionsPanel.add(orderItemAmount);
        orderItemAmount.setColumns(10);
        orderItemAmount.setText(orderItem.getQuantity() + "");

        JButton btnIncreaseAmount = ColorStyle.newButton("Increment >>");
        btnIncreaseAmount.addActionListener(event -> {
            try {
                int quantity = Integer.parseInt(orderItemAmount.getText());
                quantity++;
                orderItemAmount.setText(quantity + "");
            } catch (NumberFormatException e) {
                PopUp.newPopUp(this, "Make sure the value is numeric", "Cannot continue", PopUp.PopUpType.WARNING);
            }
        });
        optionsPanel.add(btnIncreaseAmount);

        JButton btnConfirmAmount = ColorStyle.newButton("Confirm");
        btnConfirmAmount.addActionListener(event -> {
            try {
                int quantity = Integer.parseInt(orderItemAmount.getText());

                if (quantity < 0) {
                    WarehouseOrderItem revisionOrderItem = new WarehouseOrderItem(
                            orderItem.getQuantity(),
                            orderItem.getUnitPrice(),
                            orderItem.getProduct()
                    );
                    order.removeWarehouseOrderItem(orderItem);
                }
                init();
            } catch (NumberFormatException e) {
                PopUp.newPopUp(this, "Make sure the value is numeric", "Cannot continue", PopUp.PopUpType.WARNING);
            }
        });
        optionsPanel.add(btnConfirmAmount);

        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    private void loadOrderItems() {
        DefaultListModel<WarehouseOrderItem> orderItemListModel = new DefaultListModel<>();
        List<WarehouseOrderItem> dataList = order.getItems();
        for (WarehouseOrderItem warehouseOrderItem : dataList) {
            orderItemListModel.addElement(warehouseOrderItem);
        }

        warehouseOrderItemList.setModel(orderItemListModel);

        warehouseOrderItemList.setCellRenderer(new ListCell());

    }

    private String[] loadStatuses() {
        Status[] statuses = Status.values();
        String[] options = new String[statuses.length];

        for (int i = 0; i < options.length; i++) {
            options[i] = statuses[i].value;
        }

        return options;
    }
}
