package gui;

import controller.AddressController;
import controller.StoreController;
import database.DataAccessException;
import model.Address;
import model.Store;

import javax.swing.*;
import java.awt.*;

public class UpdateStoreMenu extends JPanel {
    private final JTextField name;
    private final JTextField email;
    private final JPasswordField password;
    private final JPanel panel;
    private final JLabel lblStoreView;
    private final JPanel addressPane;
    private final JPanel namePane;
    private final JPanel emailPane;
    private final JPanel passwordPane;
    private final JTextField city;
    private final JTextField zipcode;
    private final JTextField street;
    private final JTextField number;
    private final JTextField supplement;
    private Store store;

    /**
     * Create the panel.
     */
    public UpdateStoreMenu(Store p) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));

        JPanel northPane = new JPanel();
        northPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        lblStoreView = new JLabel("New Store");
        lblStoreView.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblStoreView.setBounds(10, 10, 132, 24);
        northPane.add(lblStoreView);

        panel.add(northPane, BorderLayout.NORTH);
        JPanel centerPane = new JPanel();
        centerPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        namePane = new JPanel();
        namePane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JLabel lblName = new JLabel("Name: ");
        lblName.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblName.setBounds(70, 60, 72, 16);
        namePane.add(lblName);

        name = new JTextField();
        name.setBounds(148, 60, 116, 22);
        namePane.add(name);
        name.setColumns(20);
        centerPane.add(namePane);

        emailPane = new JPanel();
        emailPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JLabel lblEmail = new JLabel("Email: ");
        lblEmail.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblEmail.setBounds(70, 130, 72, 16);
        emailPane.add(lblEmail);

        email = new JTextField();
        email.setBounds(148, 130, 116, 22);
        emailPane.add(email);
        email.setColumns(20);
        centerPane.add(emailPane);

        passwordPane = new JPanel();
        passwordPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JLabel lblPassword = new JLabel("Password: ");
        lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblPassword.setBounds(70, 200, 72, 16);
        passwordPane.add(lblPassword);

        password = new JPasswordField();
        password.setBounds(148, 200, 116, 22);
        passwordPane.add(password);
        password.setColumns(20);
        centerPane.add(passwordPane);

        panel.add(centerPane, BorderLayout.CENTER);

        JPanel southPane = new JPanel();
        southPane.setLayout(new BorderLayout(0, 0));

        addressPane = new JPanel();
        addressPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JLabel zipcode = new JLabel("Zipcode: ");
        zipcode.setFont(new Font("Tahoma", Font.PLAIN, 13));
        zipcode.setBounds(70, 130, 72, 16);
        addressPane.add(zipcode);

        this.zipcode = new JTextField();
        this.zipcode.setBounds(148, 130, 116, 22);
        addressPane.add(this.zipcode);
        this.zipcode.setColumns(10);

        JLabel lblCity = new JLabel("City: ");
        lblCity.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblCity.setBounds(70, 60, 72, 16);
        addressPane.add(lblCity);

        city = new JTextField();
        city.setBounds(148, 60, 116, 22);
        addressPane.add(city);
        city.setColumns(10);

        JLabel lblStreet = new JLabel("Street: ");
        lblStreet.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblStreet.setBounds(70, 165, 72, 16);
        addressPane.add(lblStreet);

        street = new JTextField();
        street.setBounds(148, 165, 116, 22);
        addressPane.add(street);
        street.setColumns(10);

        JLabel lblNumber = new JLabel("Number: ");
        lblNumber.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblNumber.setBounds(70, 200, 72, 16);
        addressPane.add(lblNumber);

        number = new JTextField();
        number.setBounds(148, 200, 116, 22);
        addressPane.add(number);
        number.setColumns(10);

        JLabel lblSupplement = new JLabel("Supplement: ");
        lblSupplement.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblSupplement.setBounds(70, 235, 72, 16);
        addressPane.add(lblSupplement);

        supplement = new JTextField();
        supplement.setBounds(148, 200, 116, 22);
        addressPane.add(supplement);
        supplement.setColumns(10);
        southPane.add(addressPane, BorderLayout.NORTH);

        JPanel buttonPane = new JPanel();
        buttonPane.setBounds(0, 232, 436, 31);
        buttonPane.setBackground(Color.LIGHT_GRAY);
        buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        southPane.add(buttonPane, BorderLayout.SOUTH);
        JButton okButton = ColorStyle.newButton("Save");
        buttonPane.add(okButton);
        okButton.addActionListener(e -> okClicked());
        JButton cancelButton = ColorStyle.newButton("Cancel");
        buttonPane.add(cancelButton);
        cancelButton.addActionListener(e -> goBack());

        panel.add(southPane, BorderLayout.SOUTH);
        setVisible(true);

        init(p);
        add(panel);
    }

    private void init(Store p) {
        this.store = p;
        //System.out.println("yep");
        if (p != null) {
            fillFields();
        }
    }

    private void fillFields() {
        lblStoreView.setText("Edit store");
        this.name.setText("" + store.getName());
        this.email.setText("" + store.getEmail());
        this.password.setText(store.getPassword());
        this.city.setText(store.getAddress().getCity());
        this.zipcode.setText(store.getAddress().getZipcode());
        this.street.setText(store.getAddress().getStreet());
        this.number.setText(store.getAddress().getNumber());
        this.supplement.setText(store.getAddress().getSupplement());
    }

    private void goBack() {
        LayoutChangeMonitor.getInstance().setLayout(new StoreMenu(), "stores");
    }

    private void okClicked() {
        try {
            String name = this.name.getText();
            String email = this.email.getText();
            String password = String.valueOf(this.password.getPassword());

            StoreController storeController = new StoreController();
            AddressController addressController = new AddressController();
            Address address;
            if (store != null) {
                address = new Address(store.getAddress().getId(), this.number.getText(), this.supplement.getText(), this.street.getText(),
                        this.city.getText(), this.zipcode.getText(), "Slovakia", "Slovakia");
                addressController.updateAddress(address);
                Store p = new Store(store.getId(), name, password, email, address);
                storeController.updateStore(p);
                store = p;
                PopUp.newPopUp(this, "Store updated successfully!", "Success", PopUp.PopUpType.INFORMATION);
            } else {
                int addressID;
                address = new Address(this.number.getText(), this.supplement.getText(), this.street.getText(),
                        this.city.getText(), this.zipcode.getText(), "Slovakia", "Slovakia");
                addressID = addressController.createAddress(address);
                address.setId(addressID);
                Store p = new Store(name, email, password, address);
                int id = storeController.createStore(p);
                p.setId(id);
                store = p;
                PopUp.newPopUp(this, "Store created successfully!", "Success", PopUp.PopUpType.INFORMATION);
            }
        } catch (NumberFormatException | DataAccessException nfe) {
            PopUp.newPopUp(this, nfe.getMessage(), "Error", PopUp.PopUpType.WARNING);
        }
        setVisible(false);
        LayoutChangeMonitor.getInstance().setLayout(new StoreMenu(), "stores");
    }

}
