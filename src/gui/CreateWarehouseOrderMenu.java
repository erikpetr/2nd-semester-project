package gui;


import database.*;
import model.*;
import controller.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;

// TODO: Make this menu actually work
public class CreateWarehouseOrderMenu extends JPanel {
    private WarehouseOrderController warehouseOrderController;
    private ProductController productController;

    private final JList<Product> productList;
    private final JList<WarehouseOrderItem> warehouseOrderItemList;
    private final JLabel lblProvider;
    private final JPanel optionsPanel;
    private JTextField warehouseOrderItemAmount;
    private JTextField productAmount;

    /**
     * Create the panel.
     */
    public CreateWarehouseOrderMenu() {
        try {
            warehouseOrderController = new WarehouseOrderController();
            productController = new ProductController();
            warehouseOrderController.createWarehouseOrder((Warehouse) LoginController.getLoggedInUser());
        } catch (DataAccessException | ControlException e) {
            PopUp.newPopUp(this, e.getMessage(), "Error", PopUp.PopUpType.ERROR);
        }
        setLayout(new BorderLayout(0, 0));

        optionsPanel = new JPanel();
        add(optionsPanel, BorderLayout.NORTH);

        JSplitPane productPane = new JSplitPane();
        productPane.setResizeWeight(0.5);
        productPane.setContinuousLayout(true);
        add(productPane, BorderLayout.CENTER);

        productList = new JList<>();
        productList.addListSelectionListener(e -> {
            if (productList.getSelectedValue() != null) {
                showControlsFor(productList.getSelectedValue());
            }
        });
        productPane.setLeftComponent(productList);

        warehouseOrderItemList = new JList<>();
        warehouseOrderItemList.addListSelectionListener(e -> {
            if (warehouseOrderItemList.getSelectedValue() != null) {
                showControlsFor(warehouseOrderItemList.getSelectedValue());
            }
        });
        productPane.setRightComponent(warehouseOrderItemList);

        JPanel sidePanel = new JPanel();
        add(sidePanel, BorderLayout.EAST);
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));
        JButton btnChooseProvider = ColorStyle.newButton("Choose provider");
        btnChooseProvider.addActionListener(button -> chooseProvider());

        lblProvider = new JLabel("Choose a provider");
        sidePanel.add(lblProvider);
        sidePanel.add(btnChooseProvider);

        JButton btnCancelWarehouseOrder = ColorStyle.newCancelButton("Cancel");
        btnCancelWarehouseOrder.addActionListener(actionEvent -> cancelWarehouseOrder());
        sidePanel.add(btnCancelWarehouseOrder);

        JButton btnSubmitWarehouseOrder = ColorStyle.newButton("Submit");
        btnSubmitWarehouseOrder.addActionListener(button -> createWarehouseOrder());
        sidePanel.add(btnSubmitWarehouseOrder);

        loadProducts();
    }

    public void init() {
        updateWarehouseOrderStatus();
        loadWarehouseOrderItems();
        resetOptionsPanel();
    }

    public void updateWarehouseOrderStatus() {
        if (warehouseOrderController.getWarehouseOrder() != null) {
            lblProvider.setText("Total price: "+warehouseOrderController.getWarehouseOrder().calculateTotalPrice());
        }
    }

    private void showControlsFor(Product product) {
        optionsPanel.removeAll();

        JLabel lblAmountLabel = new JLabel("Amount:");
        optionsPanel.add(lblAmountLabel);

        productAmount = new JTextField();
        optionsPanel.add(productAmount);
        productAmount.setColumns(10);
        productAmount.setText("1");

        JLabel lblUnitPrice = new JLabel("Unit price: ");
        optionsPanel.add(lblUnitPrice);
        JTextField inputUnitPrice = new JTextField(product.getPrice()+"");
        inputUnitPrice.setColumns(10);
        optionsPanel.add(inputUnitPrice);

        JButton btnAddProduct = ColorStyle.newButton("Add product");
        btnAddProduct.addActionListener(event -> {
            try {
                int amount = Integer.parseInt(productAmount.getText());
                double unitPrice = Double.parseDouble(inputUnitPrice.getText());
                try {
                    warehouseOrderController.addProduct(product, unitPrice, amount);
                } catch (ControlException e) {
                    PopUp.newPopUp(this, e.getMessage(), "Warning", PopUp.PopUpType.WARNING);
                }
                init();
            }catch (NumberFormatException e) {
                PopUp.newPopUp(this, e.getMessage(), "Warning", PopUp.PopUpType.WARNING);
            }
        });
        optionsPanel.add(btnAddProduct);

        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    private void showControlsFor(WarehouseOrderItem warehouseOrderItem) {
        int startQuantity = warehouseOrderItem.getQuantity();
        optionsPanel.removeAll();

        JButton btnRemoveItem = ColorStyle.newCancelButton("Remove item");
        btnRemoveItem.addActionListener(e -> {
            warehouseOrderController.removeProduct(warehouseOrderItem.getProduct());
            PopUp.newPopUp(this, "Order updated", "Success", PopUp.PopUpType.INFORMATION);
            init();
        });
        optionsPanel.add(btnRemoveItem);

        JButton btnDecreaseAmount = ColorStyle.newButton("<< Decrement");
        btnDecreaseAmount.addActionListener(e -> {
            int quantity = Integer.parseInt(warehouseOrderItemAmount.getText());
            quantity--;
            warehouseOrderItemAmount.setText(quantity+"");
        });
        optionsPanel.add(btnDecreaseAmount);

        warehouseOrderItemAmount = new JTextField();
        optionsPanel.add(warehouseOrderItemAmount);
        warehouseOrderItemAmount.setColumns(10);
        warehouseOrderItemAmount.setText(warehouseOrderItem.getQuantity()+"");

        JButton btnIncreaseAmount = ColorStyle.newButton("Increment >>");
        btnIncreaseAmount.addActionListener(e -> {
            int quantity = Integer.parseInt(warehouseOrderItemAmount.getText());
            quantity++;
            warehouseOrderItemAmount.setText(quantity+"");
        });
        optionsPanel.add(btnIncreaseAmount);

        JLabel lblUnitPrice = new JLabel("Unit price: ");
        optionsPanel.add(lblUnitPrice);
        JTextField inputUnitPrice = new JTextField(warehouseOrderItem.getUnitPrice()+"");
        inputUnitPrice.setColumns(10);
        optionsPanel.add(inputUnitPrice);

        JButton btnConfirmAmount = ColorStyle.newButton("Increment >>");
        btnConfirmAmount.addActionListener(event -> {
            try {
                int quantity = Integer.parseInt(warehouseOrderItemAmount.getText());
                double unitPrice = Double.parseDouble(inputUnitPrice.getText());
                if (startQuantity < quantity) {
                    warehouseOrderController.addProduct(warehouseOrderItem.getProduct(), unitPrice, quantity - startQuantity);
                } else {
                    warehouseOrderController.removeProduct(warehouseOrderItem.getProduct(), startQuantity - quantity);
                }
                PopUp.newPopUp(this, "Warehouse order updated", "Success", PopUp.PopUpType.INFORMATION);
            } catch (ControlException e) {
                PopUp.newPopUp(this, e.getMessage(), "Warning", PopUp.PopUpType.WARNING);
            }
            init();
        });
        optionsPanel.add(btnConfirmAmount);

        optionsPanel.revalidate();
        optionsPanel.repaint();
    }
    private void chooseProvider() {
        CreateWhOrderProviderChooser.main(this, warehouseOrderController);
    }

    private void resetOptionsPanel() {
        optionsPanel.removeAll();
        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    private void loadWarehouseOrderItems() {
        if (warehouseOrderController.getWarehouseOrder() != null) {
            DefaultListModel<WarehouseOrderItem> warehouseOrderItemListModel = new DefaultListModel<>();
            List<WarehouseOrderItem> dataList = warehouseOrderController.getWarehouseOrder().getItems();
            for(WarehouseOrderItem orderItem : dataList) {
                warehouseOrderItemListModel.addElement(orderItem);
            }

            warehouseOrderItemList.setModel(warehouseOrderItemListModel);

            warehouseOrderItemList.setCellRenderer(new ListCell());
        }
    }

    private void loadProducts() {
        new ProductLoader().start();
    }

    private void cancelWarehouseOrder() {
        warehouseOrderController.setWarehouseOrder(null);
        EventQueue.invokeLater(() -> {
            this.setVisible(false);
            WarehouseOrderMenu orderMenu = new WarehouseOrderMenu();
            LayoutChangeMonitor.getInstance().setLayout(orderMenu, "warehouse_order_menu");
        });
    }

    private void createWarehouseOrder() {
        try {
            if (warehouseOrderController.getWarehouseOrder().getProvider() == null) {
                PopUp.newPopUp(this, "Please choose the provider", "Can't finish warehouse order", PopUp.PopUpType.WARNING);
            } else {
                int orderId = warehouseOrderController.finishWarehouseOrder();
                EventQueue.invokeLater(() -> {
                    WarehouseOrderFinishedScreen finishedScreen = new WarehouseOrderFinishedScreen(orderId);
                    LayoutChangeMonitor.getInstance().setLayout(finishedScreen, "warehouse_order_finished");
                });
            }
        } catch (ControlException e) {
            PopUp.newPopUp(this, e.getMessage(), "Can't finish warehouse order", PopUp.PopUpType.WARNING);
        }

    }

    private class ProductLoader extends Thread {

        @Override
        public void run() {
            if (warehouseOrderController.getWarehouseOrder() != null) {
                try {
                    DefaultListModel<Product> productListModel = new DefaultListModel<>();
                    Warehouse warehouse = warehouseOrderController.getWarehouseOrder().getWarehouse();
                    List<Product> dataList = productController.getProducts(warehouse);
                    for(Product product : dataList) {
                        productListModel.addElement(product);
                    }

                    productList.setModel(productListModel);

                    productList.setCellRenderer(new ListCell());
                } catch (DataAccessException e) {
                    PopUp.newPopUp(getThis(), e.getMessage(), "Error", PopUp.PopUpType.ERROR);
                }
            }
        }
    }

    private CreateWarehouseOrderMenu getThis() {
        return this;
    }
}
