package gui;

import controller.StoreController;
import database.DataAccessException;
import model.Store;

import javax.swing.*;
import java.awt.*;

public class UpdateStoreMenu extends JPanel {
	private final JTextField name;
	private final JTextField email;
	private final JTextField password;
	private final JTextField address;
	private final JPanel panel;
	private Store store;
	private JLabel lblStoreView;

	/**
	 * Create the panel.
	 */
	public UpdateStoreMenu(Store s) {
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

		lblStoreView = new JLabel("New Store");
		lblStoreView.setFont(new Font("Tahoma", Font.BOLD, 15));
		lblStoreView.setBounds(10, 10, 132, 24);
		panel.add(lblStoreView);

		JLabel lblName = new JLabel("Name: ");
		lblName.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblName.setBounds(70, 60, 72, 16);
		panel.add(lblName);

		name = new JTextField();
		name.setBounds(148, 60, 116, 22);
		panel.add(name);
		name.setColumns(10);

		JLabel lblMail = new JLabel("Mail: ");
		lblMail.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblMail.setBounds(70, 130, 72, 16);
		panel.add(lblMail);


		JLabel lblPassword = new JLabel("Password: ");
		lblPassword.setBounds(70, 165, 72, 16);
		panel.add(lblPassword);

		JLabel lblAddress = new JLabel("Address: ");
		lblAddress.setBounds(70, 200, 72, 16);
		panel.add(lblAddress);


		email = new JTextField();
		email.setBounds(148, 130, 116, 22);
		panel.add(email);
		email.setColumns(10);

		password = new JTextField();
		password.setBounds(148, 165, 116, 22);
		panel.add(password);
		password.setColumns(10);

		address = new JTextField();
		address.setBounds(148, 200, 116, 22);
		panel.add(address);
		address.setColumns(10);

		setVisible(true);

		init(s);
		add(panel);
	}

	private void init(Store s) {
		this.store = s;
		System.out.println("yep");
		if (s != null) {
			fillFields();
		}
	}

	private void fillFields() {
		lblStoreView.setText("Edit store");
		this.name.setText("" + store.getName());
		this.email.setText("" + store.getEmail());
		this.password.setText("" + store.getPassword());
		this.address.setText("" + store.getAddress().toString());
	}

	private void goBack() {
		this.setVisible(false);
	}

	private void okClicked() {
		try {
			String name = this.name.getText();
			String mail = this.email.getText();
			String password = this.password.getText();
			String Address = this.address.getText();
			StoreController sctrl = new StoreController();
			if (store != null) {
				Store s = new Store(name, password, mail, null);
				sctrl.updateStore(s);
			} else {
				Store s = new Store(name, password, mail, null);
				sctrl.createStore(s);
			}
		} catch (NumberFormatException | DataAccessException nfe) {
			JOptionPane.showMessageDialog(null, "Number for price and amount, please enter number");
		}
		this.setVisible(false);
	}

}
