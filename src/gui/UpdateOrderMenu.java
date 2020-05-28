package gui;

import controller.ControlException;
import controller.OrderController;
import database.DataAccessException;
import gui.PopUp.PopUpType;
import model.Order;
import model.OrderItem;
import model.OrderRevision;
import model.Status;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

public class UpdateOrderMenu extends JPanel {

    private final JPanel optionsPanel;
    private final JTextArea textRevisionNote;
    private final JList<OrderItem> orderItemList;
    private final JComboBox<String> statusBox;
    private JTextField orderItemAmount;
    private OrderController controller;
    private Order order;
    private OrderRevision revision;

    /**
     * Create the panel.
     */
    public UpdateOrderMenu(int orderId) {

        try {
            controller = new OrderController();
            order = controller.getOrder(orderId);
            revision = new OrderRevision(order);
        } catch (DataAccessException e) {
            PopUp.newPopUp(this, e.getMessage(), "Connection failed", PopUpType.ERROR);
        }

        setLayout(new BorderLayout(0, 0));

        optionsPanel = new JPanel();
        add(optionsPanel, BorderLayout.NORTH);

        JSplitPane splitPane = new JSplitPane();
        splitPane.setResizeWeight(0.5);
        add(splitPane, BorderLayout.CENTER);

        orderItemList = new JList<>();
        orderItemList.addListSelectionListener(listSelectionEvent -> {
            if (!listSelectionEvent.getValueIsAdjusting() && orderItemList.getSelectedValue() != null) {
                showControlsFor(orderItemList.getSelectedValue());
            }
        });
        splitPane.setLeftComponent(orderItemList);

        JPanel controlPanel = new JPanel();
        splitPane.setRightComponent(controlPanel);
        controlPanel.setLayout(new BoxLayout(controlPanel, BoxLayout.Y_AXIS));

        JLabel lblOrderId = new JLabel("Order " + orderId);
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
            OrderInformationMenu informationMenu = new OrderInformationMenu(orderId);
            LayoutChangeMonitor.getInstance().setLayout(informationMenu, "order_info");
        });
        buttonPanel.add(btnCancelButton);

        init();
    }

    public void init() {
        loadOrderItems();
    }

    public void submit() {
        try {
            Status status = Status.get((String) statusBox.getSelectedItem());
            revision.setNote(textRevisionNote.getText());
            revision.setStatus(status);
            revision.setDate(LocalDateTime.now());
            order.addRevision(revision);
            order.setStatus(status);
            controller.updateOrder(order);
            OrderInformationMenu informationMenu = new OrderInformationMenu(order.getId());
            LayoutChangeMonitor.getInstance().setLayout(informationMenu, "order_info");
        } catch (ControlException e) {
            PopUp.newPopUp(this, e.getMessage(), "Can't update order", PopUpType.ERROR);
        }
    }

    private void showControlsFor(OrderItem orderItem) {
        int startQuantity = orderItem.getQuantity();
        optionsPanel.removeAll();

        JButton btnRemoveItem = ColorStyle.newButton("RemoveOrderItem");
        btnRemoveItem.addActionListener(e -> {
            order.removeOrderItem(orderItem);
            OrderItem revisionItem = new OrderItem(
                    orderItem.getProduct(),
                    orderItem.getUnitPrice(),
                    -orderItem.getQuantity()
            );
            revision.addItemChanged(revisionItem);

            PopUp.newPopUp(this, "Order updated", "Success", PopUpType.INFORMATION);
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
                PopUp.newPopUp(this, "Make sure the value is numeric", "Cannot continue", PopUpType.WARNING);
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
                PopUp.newPopUp(this, "Make sure the value is numeric", "Cannot continue", PopUpType.WARNING);
            }
        });
        optionsPanel.add(btnIncreaseAmount);

        JButton btnConfirmAmount = ColorStyle.newButton("Confirm");
        btnConfirmAmount.addActionListener(event -> {
            try {
                int quantity = Integer.parseInt(orderItemAmount.getText());


                // TODO: Check this logic
                if (quantity < 0) {
                    OrderItem revisionOrderItem = new OrderItem(
                            orderItem.getProduct(),
                            orderItem.getUnitPrice(),
                            -orderItem.getQuantity()
                    );

                    revision.addItemChanged(revisionOrderItem);
                    order.removeOrderItem(orderItem);
                } else {
                    int diff = orderItem.getQuantity() - quantity;
                    if (revision.hasOrderItem(orderItem)) {
                        revision.addQuantity(orderItem, diff);
                    } else {
                        OrderItem revisionOrderItem = new OrderItem(
                                orderItem.getProduct(),
                                orderItem.getUnitPrice(),
                                diff
                        );
                        revision.addItemChanged(revisionOrderItem);
                    }
                    order.setQuantity(orderItem, quantity);
                }
                init();
            } catch (NumberFormatException e) {
                PopUp.newPopUp(this, "Make sure the value is numeric", "Cannot continue", PopUpType.WARNING);
            }
        });
        optionsPanel.add(btnConfirmAmount);

        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    private void loadOrderItems() {
        DefaultListModel<OrderItem> orderItemListModel = new DefaultListModel<>();
        List<OrderItem> dataList = order.getItems();
        for (OrderItem orderItem : dataList) {
            orderItemListModel.addElement(orderItem);
        }

        orderItemList.setModel(orderItemListModel);

        orderItemList.setCellRenderer(new ListCell());

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
