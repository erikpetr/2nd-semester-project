package gui;

import controller.ProviderController;
import database.DataAccessException;
import model.Provider;

import javax.swing.*;
import java.awt.*;

public class UpdateProviderMenu extends JPanel {
    private final JTextField name;
    private final JTextField email;
    private final JTextField address;
    private final JCheckBox available;
    private final JPanel panel;
    private final JLabel lblProviderView;
    private Provider provider;

    /**
     * Create the panel.
     */
    public UpdateProviderMenu(Provider p) {
        setLayout(new FlowLayout());
        panel = new JPanel();
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setBounds(0, 232, 436, 31);
            buttonPane.setBackground(Color.LIGHT_GRAY);
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            panel.add(buttonPane);
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
        }

        lblProviderView = new JLabel("New Provider");
        lblProviderView.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblProviderView.setBounds(10, 10, 132, 24);
        panel.add(lblProviderView);

        name = new JTextField();
        name.setBounds(148, 60, 116, 22);
        panel.add(name);
        name.setColumns(10);

        email = new JTextField();
        email.setBounds(148, 130, 116, 22);
        panel.add(email);
        email.setColumns(10);

        address = new JTextField();
        address.setBounds(148, 165, 116, 22);
        panel.add(address);
        address.setColumns(10);

        available = new JCheckBox();
        available.setBounds(148, 200, 116, 22);
        panel.add(available);


        JLabel lblName = new JLabel("Name: ");
        lblName.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblName.setBounds(70, 60, 72, 16);
        panel.add(lblName);

        JLabel lblEmail = new JLabel("Email: ");
        lblEmail.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblEmail.setBounds(70, 130, 72, 16);
        panel.add(lblEmail);

        JLabel lblAddress = new JLabel("Address: ");
        lblAddress.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblAddress.setBounds(70, 165, 72, 16);
        panel.add(lblAddress);

        JLabel lblAvailable = new JLabel("Available: ");
        lblAvailable.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblAvailable.setBounds(70, 200, 72, 16);
        panel.add(lblAvailable);


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
        this.address.setText("" + provider.getAddress().toString());
        this.available.setSelected(provider.getAvailable());
    }

    private void goBack() {
        this.setVisible(false);
    }

    private void okClicked() {
        try {
            String name = this.name.getText();
            String email = this.email.getText();
            //String address = this.address.getText();
            ProviderController providerController = new ProviderController();
            if (provider != null) {
                Provider p = new Provider(name, email, available.isSelected(), null);
                providerController.updateProvider(p);
            } else {
                Provider p = new Provider(name, email, available.isSelected(), null);
                providerController.createProvider(p);
            }
        } catch (NumberFormatException | DataAccessException nfe) {
            PopUp.newPopUp(this, nfe.getMessage(), "Error", PopUp.PopUpType.WARNING);
        }
        this.setVisible(false);
    }

}
