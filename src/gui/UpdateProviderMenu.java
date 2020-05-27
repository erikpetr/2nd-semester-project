package gui;

import controller.AddressController;
import controller.ProviderController;
import database.DataAccessException;
import model.Address;
import model.Provider;

import javax.swing.*;
import java.awt.*;

public class UpdateProviderMenu extends JPanel {
    private final JTextField name;
    private final JTextField email;
    private final JCheckBox available;
    private final JPanel panel;
    private final JLabel lblProviderView;
    private final JPanel addressPane;
    private final JPanel namePane;
    private final JPanel emailPane;
    private final JPanel availablePane;
    private final JTextField city;
    private final JTextField zipcode;
    private final JTextField street;
    private final JTextField number;
    private final JTextField supplement;
    private Provider provider;

    /**
     * Create the panel.
     */
    public UpdateProviderMenu(Provider p) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 0));

        JPanel northPane = new JPanel();
        northPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

        lblProviderView = new JLabel("New Provider");
        lblProviderView.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblProviderView.setBounds(10, 10, 132, 24);
        northPane.add(lblProviderView);

        panel.add(northPane, BorderLayout.NORTH);
        {
            JPanel centerPane = new JPanel();
            centerPane.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));

            namePane = new JPanel();
            namePane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            {
                JLabel lblName = new JLabel("Name: ");
                lblName.setFont(new Font("Tahoma", Font.PLAIN, 13));
                lblName.setBounds(70, 60, 72, 16);
                namePane.add(lblName);

                name = new JTextField();
                name.setBounds(148, 60, 116, 22);
                namePane.add(name);
                name.setColumns(20);
            }
            centerPane.add(namePane);

            emailPane = new JPanel();
            emailPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            {
                JLabel lblEmail = new JLabel("Email: ");
                lblEmail.setFont(new Font("Tahoma", Font.PLAIN, 13));
                lblEmail.setBounds(70, 130, 72, 16);
                emailPane.add(lblEmail);

                email = new JTextField();
                email.setBounds(148, 130, 116, 22);
                emailPane.add(email);
                email.setColumns(20);
            }
            centerPane.add(emailPane);

            availablePane = new JPanel();
            availablePane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            {
                JLabel lblAvailable = new JLabel("Available: ");
                lblAvailable.setFont(new Font("Tahoma", Font.PLAIN, 13));
                lblAvailable.setBounds(70, 200, 72, 16);
                availablePane.add(lblAvailable);

                available = new JCheckBox();
                available.setBounds(148, 200, 116, 22);
                availablePane.add(available);
            }
            centerPane.add(availablePane);

            panel.add(centerPane, BorderLayout.CENTER);

            JPanel southPane = new JPanel();
            southPane.setLayout(new BorderLayout(0, 0));

            addressPane = new JPanel();
            addressPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            {
                JLabel zipcode_1 = new JLabel("Zipcode: ");
                zipcode_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
                zipcode_1.setBounds(70, 130, 72, 16);
                addressPane.add(zipcode_1);

                zipcode = new JTextField();
                zipcode.setBounds(148, 130, 116, 22);
                addressPane.add(zipcode);
                zipcode.setColumns(10);

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
            }
            southPane.add(addressPane, BorderLayout.NORTH);

            JPanel buttonPane = new JPanel();
            buttonPane.setBounds(0, 232, 436, 31);
            buttonPane.setBackground(Color.LIGHT_GRAY);
            buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            southPane.add(buttonPane, BorderLayout.SOUTH);
            {
                JButton okButton = new JButton("Save");
                buttonPane.add(okButton);
                okButton.addActionListener(e -> okClicked());
            }
            {
                JButton cancelButton = new JButton("Cancel");
                buttonPane.add(cancelButton);
                cancelButton.addActionListener(e -> goBack());
            }

            panel.add(southPane, BorderLayout.SOUTH);
        }
        setVisible(true);

        init(p);
        add(panel);
    }

    private void init(Provider p) {
        this.provider = p;
        System.out.println("yep");
        if (p != null) {
            fillFields();
        }
    }

    private void fillFields() {
        lblProviderView.setText("Edit provider");
        this.name.setText("" + provider.getName());
        this.email.setText("" + provider.getEmail());
        this.available.setSelected(provider.getAvailable());
        this.city.setText(provider.getAddress().getCity());
        this.zipcode.setText(provider.getAddress().getZipcode());
        this.street.setText(provider.getAddress().getStreet());
        this.number.setText(provider.getAddress().getNumber());
        this.supplement.setText(provider.getAddress().getSupplement());
    }

    private void goBack() {
        LayoutChangeMonitor.getInstance().setLayout(new ProviderMenu(), "providers");
    }

    private void okClicked() {
        try {
            String name = this.name.getText();
            String email = this.email.getText();

            ProviderController providerController = new ProviderController();
            AddressController addressController = new AddressController();
            Address address;
            if (provider != null) {
                address = new Address(provider.getAddress().getId(), this.number.getText(), this.supplement.getText(), this.street.getText(),
                        this.city.getText(), this.zipcode.getText(), "Slovakia", "Slovakia");
                addressController.updateAddress(address);
                Provider p = new Provider(provider.getId(), name, email, available.isSelected(), address);
                providerController.updateProvider(p);
                provider = p;
                PopUp.newPopUp(this, "Provider updated successfully!", "Success", PopUp.PopUpType.INFORMATION);
            } else {
                int addressID;
                address = new Address(this.number.getText(), this.supplement.getText(), this.street.getText(),
                        this.city.getText(), this.zipcode.getText(), "Slovakia", "Slovakia");
                addressID = addressController.createAddress(address);
                address.setId(addressID);
                Provider p = new Provider(name, email, available.isSelected(), address);
                int id = providerController.createProvider(p);
                p.setId(id);
                provider = p;
                PopUp.newPopUp(this, "Provider created successfully!", "Success", PopUp.PopUpType.INFORMATION);
            }
        } catch (NumberFormatException | DataAccessException nfe) {
            PopUp.newPopUp(this, nfe.getMessage(), "Error", PopUp.PopUpType.WARNING);
        }
        setVisible(false);
        LayoutChangeMonitor.getInstance().setLayout(new ProviderMenu(), "providers");
    }

}
