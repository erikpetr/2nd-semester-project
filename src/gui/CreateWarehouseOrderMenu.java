package gui;

import controller.ControlException;
import controller.ProductController;
import controller.WarehouseOrderController;
import database.DataAccessException;
import model.Product;
import model.Warehouse;
import model.WarehouseOrderItem;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class CreateWarehouseOrderMenu extends JPanel {
    private WarehouseOrderController warehouseOrderController;
    private ProductController productController;
    private Product product;

    private DefaultListModel<Product> productListModel;
    private DefaultListModel<WarehouseOrderItem> warehouseOrderItemListModel;
    private final JList<Product> productList;
    private final JList<WarehouseOrderItem> warehouseOrderItemList;
    private JLabel lblProvider;
    private JPanel optionsPanel;
    private JTextField warehouseOrderItemAmount;
    private JTextField productAmount;

    /**
     * Create the panel.
     */
    public CreateWarehouseOrderMenu() {
        try {
            warehouseOrderController = new WarehouseOrderController();
            productController = new ProductController();
        } catch (DataAccessException e) {
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
            if (!e.getValueIsAdjusting()) {
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
         // TODO: we dont need this, do we?
        // TODO: implement choose provider
        JButton btnChooseProvider = new JButton("Choose provider");
        btnChooseProvider.addActionListener(button -> {
//            chooseProvider();
        });

        lblProvider = new JLabel("Choose a provider");
        sidePanel.add(lblProvider);
        sidePanel.add(btnChooseProvider);

        JButton btnCancelWarehouseOrder = new JButton("Cancel");
        btnCancelWarehouseOrder.addActionListener(actionEvent -> cancelWarehouseOrder());
        sidePanel.add(btnCancelWarehouseOrder);

        JButton btnSubmitWarehouseOrder = new JButton("Submit");
        btnSubmitWarehouseOrder.addActionListener(button -> createWarehouseOrder());
        sidePanel.add(btnSubmitWarehouseOrder);
    }

    public void init() {
        updateWarehouseOrderStatus();
        loadWarehouseOrderItems();
        loadProducts();
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

        JButton btnAddProduct = new JButton("Add product");
        btnAddProduct.addActionListener(event -> {
            int amount = Integer.parseInt(productAmount.getText());
            try {
                warehouseOrderController.addProduct(product, amount);
            } catch (ControlException e) {
                PopUp.newPopUp(this, e.getMessage(), "Warning", PopUp.PopUpType.WARNING);
            }
            init();
        });
        optionsPanel.add(btnAddProduct);

        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    private void showControlsFor(WarehouseOrderItem warehouseOrderItem) {
        int startQuantity = warehouseOrderItem.getQuantity();
        optionsPanel.removeAll();

        JButton btnRemoveItem = new JButton("RemoveWarehouseOrderItem");
        btnRemoveItem.addActionListener(e -> {
            warehouseOrderController.removeProduct(warehouseOrderItem.getProduct());
            PopUp.newPopUp(this, "Order updated", "Success", PopUp.PopUpType.INFORMATION);
            init();
        });
        optionsPanel.add(btnRemoveItem);

        JButton btnDecreaseAmount = new JButton("<< Decrement");
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

        JButton btnIncreaseAmount = new JButton("Increment >>");
        btnIncreaseAmount.addActionListener(e -> {
            int quantity = Integer.parseInt(warehouseOrderItemAmount.getText());
            quantity++;
            warehouseOrderItemAmount.setText(quantity+"");
        });
        optionsPanel.add(btnIncreaseAmount);

        JButton btnConfirmAmount = new JButton("Confirm");
        btnConfirmAmount.addActionListener(event -> {
            try {
                int quantity = Integer.parseInt(warehouseOrderItemAmount.getText());
                if (startQuantity < quantity) {
                    warehouseOrderController.addProduct(warehouseOrderItem.getProduct(), quantity - startQuantity);
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

    private void resetOptionsPanel() {
        optionsPanel.removeAll();
        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    private void loadWarehouseOrderItems() {
        if (warehouseOrderController.getWarehouseOrder() != null) {
            warehouseOrderItemListModel = new DefaultListModel<>();
            List<WarehouseOrderItem> dataList = warehouseOrderController.getWarehouseOrder().getItems();
            for(WarehouseOrderItem orderItem : dataList) {
                warehouseOrderItemListModel.addElement(orderItem);
            }

            warehouseOrderItemList.setModel(warehouseOrderItemListModel);

            warehouseOrderItemList.setCellRenderer(new ListCell());
        }
    }

    private void loadProducts() {
        new ProductLoader(this).start();
    }

    private void cancelWarehouseOrder() {
        warehouseOrderController.setWarehouseOrder(null);
        this.setVisible(false);
    }

    private void createWarehouseOrder() {
        try {
            int orderId = warehouseOrderController.finishWarehouseOrder();
            LayoutChangeMonitor.getInstance().setLayout(new WarehouseOrderFinishedScreen(orderId), "warehouse_order_finished");
        } catch (ControlException e) {
            PopUp.newPopUp(this, e.getMessage(), "Can't finish warehouse order", PopUp.PopUpType.WARNING);
        }

    }

    private class ProductLoader extends Thread {
        JComponent parent;
        public ProductLoader(JComponent parent) {
            this.parent = parent;
        }
        @Override
        public void run() {
            if (warehouseOrderController.getWarehouseOrder() != null) {
                try {
                    productListModel = new DefaultListModel<>();
                    Warehouse warehouse = warehouseOrderController.getWarehouseOrder().getWarehouse();
                    List<Product> dataList = productController.getProducts(warehouse);
                    for(Product product : dataList) {
                        productListModel.addElement(product);
                    }

                    productList.setModel(productListModel);

                    productList.setCellRenderer(new ListCell());
                } catch (DataAccessException e) {
                    PopUp.newPopUp(parent, e.getMessage(), "Error", PopUp.PopUpType.ERROR);
                }
            }
        }
    }
}
