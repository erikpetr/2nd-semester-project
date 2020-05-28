package gui;

import java.util.LinkedList;
import java.util.List;

import controller.ControlException;
import controller.StoreStockReportController;
import controller.ProductController;
import database.DataAccessException;
import gui.PopUp.PopUpType;
import java.awt.*;
import javax.swing.*;

import model.*;

public class CreateStoreStockReportMenu extends JPanel {

    private StoreStockReportController storeStockReportController;
    private ProductController productController;

    private DefaultListModel<Product> productListModel;
    private DefaultListModel<StoreStockReportItem> reportItemListModel;
    private final JList<Product> productList;
    private final JList<StoreStockReportItem> reportItemList;
    private JLabel lblOrderStatus;
    private JPanel optionsPanel;
    private JTextField orderItemAmount;
    private JTextField productAmount;

    /**
     * Create the panel.
     */
    public CreateStoreStockReportMenu () {
        try {
            storeStockReportController = new StoreStockReportController();
            storeStockReportController.createOrder();
            productController = new ProductController();
        } catch (DataAccessException e) {
            PopUp.newPopUp(this, e.getMessage(), "Error", PopUpType.ERROR);
        } catch (ControlException e) {
            PopUp.newPopUp(this, e.getMessage(), "Error", PopUpType.ERROR);
            e.printStackTrace();
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

        reportItemList = new JList<>();
        reportItemList.addListSelectionListener(e -> {
            if (reportItemList.getSelectedValue() != null) {
                showControlsFor(reportItemList.getSelectedValue());
            }
        });
        productPane.setRightComponent(reportItemList);

        JPanel sidePanel = new JPanel();
        add(sidePanel, BorderLayout.EAST);
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.PAGE_AXIS));

        lblOrderStatus = new JLabel("Choose a warehouse");
        sidePanel.add(lblOrderStatus);

        JButton btnCancelReport= ColorStyle.newCancelButton("Cancel");
        btnCancelReport.addActionListener(actionEvent -> cancelReport());
        sidePanel.add(btnCancelReport);

        JButton btnSubmitOrder = ColorStyle.newButton("Submit");
        btnSubmitOrder.addActionListener(button -> submitReport());
        sidePanel.add(btnSubmitOrder);
        init();
    }

    public void init() {
        updateReportStatus();
        loadReportItems();
        loadProducts();
        resetOptionsPanel();
    }

    public void updateReportStatus() {
        lblOrderStatus.setText("Total price: "+ storeStockReportController.getReport().calculateTotalPrice());
    }

    private void showControlsFor(Product product) {
        optionsPanel.removeAll();

        JLabel lblAmountLabel = new JLabel("Amount:");
        optionsPanel.add(lblAmountLabel);

        productAmount = new JTextField();
        optionsPanel.add(productAmount);
        productAmount.setColumns(10);
        productAmount.setText("1");

        JButton btnAddProduct = ColorStyle.newButton("Add product");
        btnAddProduct.addActionListener(event -> {
            int amount = Integer.parseInt(productAmount.getText());
            try {
                storeStockReportController.addProduct(product, amount);
            } catch (ControlException e) {
                PopUp.newPopUp(this, e.getMessage(), "Warning", PopUpType.WARNING);
            }
            init();
        });
        optionsPanel.add(btnAddProduct);

        optionsPanel.revalidate();
        optionsPanel.repaint();
    }

    private void showControlsFor(StoreStockReportItem storeStockReportItem) {
        int startQuantity = storeStockReportItem.getQuantity();
        optionsPanel.removeAll();

        JButton btnRemoveItem = ColorStyle.newCancelButton("Remove order item");
        btnRemoveItem.addActionListener(e -> {
            storeStockReportController.removeProduct(storeStockReportItem.getProduct());
            PopUp.newPopUp(this, "Report updated", "Success", PopUpType.INFORMATION);
            init();
        });
        optionsPanel.add(btnRemoveItem);

        JButton btnDecreaseAmount = ColorStyle.newButton("<< Decrement");
        btnDecreaseAmount.addActionListener(e -> {
            int quantity = Integer.parseInt(orderItemAmount.getText());
            quantity--;
            orderItemAmount.setText(quantity+"");
        });
        optionsPanel.add(btnDecreaseAmount);

        orderItemAmount = new JTextField();
        optionsPanel.add(orderItemAmount);
        orderItemAmount.setColumns(10);
        orderItemAmount.setText(storeStockReportItem.getQuantity()+"");

        JButton btnIncreaseAmount = ColorStyle.newButton("Increment >>");
        btnIncreaseAmount.addActionListener(e -> {
            int quantity = Integer.parseInt(orderItemAmount.getText());
            quantity++;
            orderItemAmount.setText(quantity+"");
        });
        optionsPanel.add(btnIncreaseAmount);

        JButton btnConfirmAmount = ColorStyle.newButton("Confirm");
        btnConfirmAmount.addActionListener(event -> {
            try {
                int quantity = Integer.parseInt(orderItemAmount.getText());
                if (startQuantity < quantity) {
                    storeStockReportController.addProduct(storeStockReportItem.getProduct(), quantity - startQuantity);
                } else {
                    storeStockReportController.removeProduct(storeStockReportItem.getProduct(), startQuantity - quantity);
                }
                PopUp.newPopUp(this, "Order updated", "Success", PopUpType.INFORMATION);
            } catch (ControlException e) {
                PopUp.newPopUp(this, e.getMessage(), "Warning", PopUpType.WARNING);
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

    private void loadReportItems() {
        StoreStockReport report = storeStockReportController.getReport();
        if (report != null) {
            reportItemListModel = new DefaultListModel<>();
            List<StoreStockReportItem> dataList = report.getItems();
            for(StoreStockReportItem reportItem : dataList) {
                reportItemListModel.addElement(reportItem);
            }

            reportItemList.setModel(reportItemListModel);

            reportItemList.setCellRenderer(new ListCell());
        }
    }

    private void loadProducts() {
        new ProductLoader(this).start();
    }

    private void cancelReport() {
        // TODO : Show all report instead of nothing when canceling
        storeStockReportController.setReport(null);
        this.setVisible(false);
    }

    private void submitReport() {
        String noteReport = (String)JOptionPane.showInputDialog(
                this,
                "Please attech a note to this report",
                "Save Report",
                JOptionPane.PLAIN_MESSAGE,
                null,
                null,
                "");

        if (noteReport != null) {
            try {
                storeStockReportController.getReport().setNote(noteReport);
                int reportId = storeStockReportController.finishReport();
                PopUp.newPopUp(this, "The report number " + reportId + " has been saved.", "Report Done", PopUpType.INFORMATION);
            } catch (ControlException e) {
                PopUp.newPopUp(this, e.getMessage(), "Can't finish order", PopUpType.WARNING);
            }
        }
    }

    private class ProductLoader extends Thread {
        JComponent parent;
        public ProductLoader(JComponent parent) {
            this.parent = parent;
        }
        @Override
        public void run() {
            try {
                if (StoreStockReportController.getReportStoreStockReportByStore() != null) {
                    try {
                        productListModel = new DefaultListModel<>();
                        List<Product> dataList = productController.all();
                        for(Product product : dataList) {
                            productListModel.addElement(product);
                        }

                        productList.setModel(productListModel);

                        productList.setCellRenderer(new ListCell());
                    } catch (DataAccessException e) {
                        PopUp.newPopUp(parent, e.getMessage(), "Error", PopUpType.ERROR);
                    }
                }
            } catch (ControlException e) {
                PopUp.newPopUp(parent, e.getMessage(), "Can't finish order", PopUpType.WARNING);
            }
        }
    }
}
