package gui;

import controller.ProductController;
import database.DataAccessException;
import model.Product;

import javax.swing.*;
import java.awt.*;

public class UpdateProductMenu extends JPanel {
    private final JTextField name;
    private final JTextField weight;
    private final JTextField price;
    private final JPanel panel;
    private Product product;
    private final JLabel lblProductView;

    /**
     * Create the panel.
     */
    public UpdateProductMenu(Product p) {
        panel = new JPanel();
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));


        setVisible(true);

        add(panel);
        panel.setLayout(new BorderLayout(0, 0));


        JPanel weightPane = new JPanel();
        panel.add(weightPane, BorderLayout.EAST);
        weightPane.setLayout(new BoxLayout(weightPane, BoxLayout.X_AXIS));


        JLabel lblWeight = new JLabel("Weight: ");
        lblWeight.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblWeight.setBounds(70, 130, 72, 16);
        weightPane.add(lblWeight);

        weight = new JTextField();
        weight.setBounds(148, 130, 116, 22);
        weightPane.add(weight);
        weight.setColumns(10);


        JPanel titlePane = new JPanel();
        panel.add(titlePane, BorderLayout.NORTH);
        titlePane.setLayout(new BoxLayout(titlePane, BoxLayout.X_AXIS));

        lblProductView = new JLabel("New Product");
        lblProductView.setFont(new Font("Tahoma", Font.BOLD, 15));
        titlePane.add(lblProductView);

        JPanel namePane = new JPanel();
        panel.add(namePane, BorderLayout.WEST);
        namePane.setLayout(new BoxLayout(namePane, BoxLayout.X_AXIS));


        JLabel lblName = new JLabel("Name: ");
        lblName.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblName.setBounds(70, 60, 72, 16);
        namePane.add(lblName);

        name = new JTextField();
        name.setBounds(148, 60, 116, 22);
        namePane.add(name);
        name.setColumns(10);


        JPanel pricePane = new JPanel();
        panel.add(pricePane);
        pricePane.setLayout(new BoxLayout(pricePane, BoxLayout.X_AXIS));

        JLabel lblPrice = new JLabel("Price: ");
        lblPrice.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblPrice.setBounds(70, 165, 72, 16);
        pricePane.add(lblPrice);

        price = new JTextField();
        price.setBounds(148, 165, 116, 22);
        pricePane.add(price);
        price.setColumns(10);

        JPanel buttonPane = new JPanel();
        buttonPane.setBackground(Color.LIGHT_GRAY);
        panel.add(buttonPane, BorderLayout.SOUTH);
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
        JButton okButton = ColorStyle.newButton("Save");
        buttonPane.add(okButton);
        okButton.addActionListener(e -> okClicked());
        JButton cancelButton = ColorStyle.newButton("Cancel");
        buttonPane.add(cancelButton);
        cancelButton.addActionListener(e -> goBack());

        init(p);

    }

    private void init(Product p) {
        this.product = p;
        //System.out.println("yep");
        if (p != null) {
            fillFields();
        }
    }

    private void fillFields() {
        lblProductView.setText("Edit Product");
        this.name.setText("" + product.getName());
        this.weight.setText("" + product.getWeight());
        this.price.setText("" + product.getPrice());
    }

    private void goBack() {
        LayoutChangeMonitor.getInstance().setLayout(new ProductMenu(), "products");
    }

    private void okClicked() {
        try {
            product.setName(this.name.getText());
            product.setWeight(Integer.parseInt(this.weight.getText()));
            product.setPrice(Double.parseDouble(this.price.getText()));
            ProductController productController = new ProductController();
            if (product != null) {
                productController.updateProduct(product);
                PopUp.newPopUp(this, "Product updated successfully!", "Success", PopUp.PopUpType.INFORMATION);
            } else {
                productController.createProduct(product);
                PopUp.newPopUp(this, "Product created successfully!", "Success", PopUp.PopUpType.INFORMATION);
            }
        } catch (NumberFormatException | DataAccessException nfe) {
            PopUp.newPopUp(this, nfe.getMessage(), "Error", PopUp.PopUpType.WARNING);
        }
        setVisible(false);
        LayoutChangeMonitor.getInstance().setLayout(new ProductMenu(), "products");
    }

}
