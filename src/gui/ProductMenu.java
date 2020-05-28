package gui;

import controller.ProductController;
import database.DataAccessException;
import gui.PopUp.PopUpType;
import model.Product;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ProductMenu extends JPanel {
    private final JSplitPane productPane;
    private final JTextPane productInfo;
    private final JList<Product> productList;
    private final JPanel optionPanel;
    private ProductController productController;
    private DefaultListModel<Product> productListModel;

    /**
     * Create the panel.
     */
    public ProductMenu() {
        try {
            productController = new ProductController();
        } catch (DataAccessException e) {
            PopUp.newPopUp(this, e.getMessage(), "Error", PopUpType.ERROR);
        }
        setLayout(new BorderLayout(0, 0));

        productPane = new JSplitPane();
        productPane.setResizeWeight(0.5);
        productPane.setContinuousLayout(true);
        add(productPane, BorderLayout.CENTER);

        productInfo = new JTextPane();
        productPane.setRightComponent(productInfo);

        productList = new JList<>();
        productList.addListSelectionListener(e -> {
            if (productList.getSelectedValue() != null) {
                showControlsFor(productList.getSelectedValue());
                showInfoFor(productList.getSelectedValue());
            }
        });
        productPane.setLeftComponent(productList);

        optionPanel = new JPanel();
        add(optionPanel, BorderLayout.NORTH);


        init();
    }


    public void init() {
        loadProducts();
        optionPanel.removeAll();
        optionPanel.revalidate();
        optionPanel.repaint();
    }

    private void showControlsFor(Product product) {
        optionPanel.removeAll();

        JButton btnCreateProduct = ColorStyle.newButton("New product");
        btnCreateProduct.addActionListener(e -> {
            createProduct();
            init();
        });
        optionPanel.add(btnCreateProduct);

        JButton btnUpdateProduct = ColorStyle.newButton("Update product");
        btnUpdateProduct.addActionListener(e -> {
            LayoutChangeMonitor.getInstance().setLayout(new UpdateProductMenu(product), "update_product");
            init();
        });
        optionPanel.add(btnUpdateProduct);

        JButton btnDeleteProduct = ColorStyle.newButton("Delete");
        btnDeleteProduct.addActionListener(e -> {
            try {
                productController.deleteProduct(product);
            } catch (DataAccessException e1) {
                PopUp.newPopUp(this, e1.getMessage(), "Error", PopUpType.ERROR);
            }
            PopUp.newPopUp(this, "Product deleted!", "Success", PopUpType.INFORMATION);
            init();
        });
        optionPanel.add(btnDeleteProduct);

        optionPanel.revalidate();
        optionPanel.repaint();

    }

    private void showInfoFor(Product selectedValue) {
        String info = "";
        info += "<html><h1><b>Product name: </b>" + selectedValue.getName() + " (ID: " + selectedValue.getId() + ")</h1></br>";
        info += "<h2>Price: " + selectedValue.getPrice() + " EUR</h2></br>";
        info += "<h2>Weight: " + selectedValue.getWeight() + "Kg</h2>";
        productInfo.setFont(new Font("Tahoma", Font.PLAIN, 14));
        productInfo.setEditable(false);
        productInfo.setContentType("text/html");
        productInfo.setText(info);
    }

    public void loadProducts() {
        new ProductLoader(this).start();
    }

    public void createProduct() {
        LayoutChangeMonitor.getInstance().setLayout(new UpdateProductMenu(null), "update_product");
    }

    private class ProductLoader extends Thread {
        JComponent parent;

        public ProductLoader(JComponent parent) {
            this.parent = parent;
        }

        @Override
        public void run() {
            try {
                productListModel = new DefaultListModel<>();
                List<Product> dataList = productController.all();
                for (Product product : dataList) {
                    productListModel.addElement(product);
                }

                productList.setModel(productListModel);

                productList.setCellRenderer(new ListCell());
            } catch (DataAccessException e) {
                PopUp.newPopUp(parent, e.getMessage(), "Error", PopUpType.ERROR);
            }
        }
    }

}
