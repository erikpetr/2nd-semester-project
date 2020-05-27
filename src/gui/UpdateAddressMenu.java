package gui;

import controller.AddressController;
import database.DataAccessException;
import model.Address;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class UpdateAddressMenu extends JPanel {
    private final JTextField city;
    private final JTextField zipcode;
    private final JTextField street;
    private final JTextField number;
    private final JTextField supplement;
    private final JPanel panel;
    private Address address;
    private final JLabel lblAddressView;

    /**
     * Create the panel.
     */
    public UpdateAddressMenu(Address a) {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(100, 100, (int) (screenSize.width * 0.8), (int) (screenSize.height * 0.8));

        setBounds(100, 100, 450, 317);
        panel = new JPanel();
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setBounds(0, 232, 436, 31);
            buttonPane.setBackground(Color.LIGHT_GRAY);
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            panel.add(buttonPane);
            {
                JButton okButton = new JButton("Save");
                okButton.setActionCommand("Save");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
                okButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        okClicked();
                    }
                });
            }
            {
                JButton cancelButton = new JButton("Cancel");
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        goBack();
                    }
                });
            }
        }

        lblAddressView = new JLabel("New Address");
        lblAddressView.setFont(new Font("Tahoma", Font.BOLD, 15));
        lblAddressView.setBounds(10, 10, 132, 24);
        panel.add(lblAddressView);

        JLabel lblCity = new JLabel("City: ");
        lblCity.setFont(new Font("Tahoma", Font.PLAIN, 13));
        lblCity.setBounds(70, 60, 72, 16);
        panel.add(lblCity);

        JLabel zipcode_1 = new JLabel("Zipcode: ");
        zipcode_1.setFont(new Font("Tahoma", Font.PLAIN, 13));
        zipcode_1.setBounds(70, 130, 72, 16);
        panel.add(zipcode_1);

        JLabel lblStreet = new JLabel("Street: ");
        lblStreet.setBounds(70, 165, 72, 16);
        panel.add(lblStreet);

        JLabel lblNumber = new JLabel("Number: ");
        lblNumber.setBounds(70, 200, 72, 16);
        panel.add(lblNumber);

        JLabel lblSupplement = new JLabel("Supplement: ");
        lblSupplement.setBounds(70, 235, 72, 16);
        panel.add(lblSupplement);

        city = new JTextField();
        city.setBounds(148, 60, 116, 22);
        panel.add(city);
        city.setColumns(10);

        this.zipcode = new JTextField();
        this.zipcode.setBounds(148, 130, 116, 22);
        panel.add(this.zipcode);
        this.zipcode.setColumns(10);

        street = new JTextField();
        street.setBounds(148, 165, 116, 22);
        panel.add(street);
        street.setColumns(10);

        number = new JTextField();
        number.setBounds(148, 200, 116, 22);
        panel.add(number);
        number.setColumns(10);

        supplement = new JTextField();
        supplement.setBounds(148, 200, 116, 22);
        panel.add(supplement);
        supplement.setColumns(10);

        init(a);
        add(panel);
    }

    private void init(Address address) {
        this.address = address;
        if (address != null) {
            fillFields();
        }
    }

    private void fillFields() {
        lblAddressView.setText("Edit Address");
        this.city.setText("" + address.getCity());
        this.zipcode.setText("" + address.getZipcode());
        this.street.setText("" + address.getStreet());
        this.number.setText("" + address.getNumber());
        this.supplement.setText("" + address.getSupplement());
    }

    private void goBack() {
        this.setVisible(false);
    }

    private void okClicked() {
        try {
            String city = this.city.getText();
            String zipcode = this.zipcode.getText();
            String street = this.street.getText();
            String number = this.number.getText();
            String supplement = this.supplement.getText();
            AddressController actrl = new AddressController();
            if (address != null) {
                Address a = new Address(number, supplement, street, city, zipcode, "Slovakia", "Slovakia");
                actrl.updateAddress(a);
            } else {
                Address a = new Address(number, supplement, street, city, zipcode, "Slovakia", "Slovakia");
                actrl.createAddress(a);
            }
        } catch (DataAccessException nfe) {
            PopUp.newPopUp(this, nfe.getMessage(), "Error", PopUp.PopUpType.ERROR);
        }
        this.setVisible(false);
    }
}
