package gui;

import controller.ControlException;
import controller.LoginController;
import database.DataAccessException;
import model.User;
import model.Warehouse;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginMenu extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private final JTextField username;
    private final JTextField password;

    /**
     * Create the dialog.
     */
    public LoginMenu() {
        setBounds(100, 100, 450, 300);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        contentPanel.setBackground(ColorStyle.BUTTON_TEXT);
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(null);
        JLabel lblUsername = new JLabel("Username:");
        lblUsername.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblUsername.setBounds(25, 33, 100, 37);
        contentPanel.add(lblUsername);

        username = new JTextField();
        username.setToolTipText("Please enter your username!");
        username.setFont(new Font("Tahoma", Font.PLAIN, 16));
        username.setBounds(123, 35, 275, 37);
        contentPanel.add(username);
        username.setColumns(10);

        JLabel lblPassword = new JLabel("Password:");
        lblPassword.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblPassword.setBounds(25, 93, 100, 37);
        contentPanel.add(lblPassword);

        password = new JPasswordField();
        password.setToolTipText("Please enter your password!");
        password.setFont(new Font("Tahoma", Font.PLAIN, 16));
        password.setBounds(123, 93, 275, 37);
        contentPanel.add(password);
        password.setColumns(2);

        JPanel buttonPane = new JPanel();
        buttonPane.setBackground(ColorStyle.BUTTON_TEXT);
        buttonPane.setLayout(new FlowLayout(FlowLayout.CENTER));
        getContentPane().add(buttonPane, BorderLayout.SOUTH);

        JButton okButton = new JButton("Login");
        okButton.setForeground(ColorStyle.BUTTON_TEXT);
        okButton.setBackground(ColorStyle.BUTTON_BACKGROUND);
        okButton.setOpaque(true);
        okButton.setBorderPainted(false);
        okButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
        buttonPane.add(okButton);
        okButton.addActionListener(e -> login());
        getRootPane().setDefaultButton(okButton);

        JButton cancelButton = new JButton("Cancel");
        cancelButton.setForeground(ColorStyle.BUTTON_TEXT);
        cancelButton.setBackground(ColorStyle.BUTTON_BACKGROUND);
        cancelButton.setOpaque(true);
        cancelButton.setBorderPainted(false);
        cancelButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
        cancelButton.setActionCommand("Cancel");
        buttonPane.add(cancelButton);

        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(ColorStyle.BUTTON_TEXT);
        getContentPane().add(titlePanel, BorderLayout.NORTH);

        JLabel lblTitle = new JLabel("Tevos Management System");
        lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
        lblTitle.setForeground(new Color(47, 36, 131));
        lblTitle.setFont(new Font("Tahoma", Font.BOLD, 18));
        titlePanel.add(lblTitle);
        cancelButton.addActionListener(e -> System.exit(0));
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(() -> {
            LoginMenu dialog = null;
            try {
                dialog = new LoginMenu();
                dialog.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
                dialog.setVisible(true);
            } catch (Exception e) {
                PopUp.newPopUp(dialog, "Couldn't start the application.\nThere has been a catastrophic error.\nThe program will terminate" +
                        e.getMessage(), "Failure", PopUp.PopUpType.ERROR);
                System.exit(-1);
            }
        });
    }

    private void login() {
        try {
            User loggedIn;
            if (LoginController.logIn(this.username.getText(), this.password.getText())) {
                loggedIn = LoginController.getLoggedInUser();

                if (loggedIn instanceof Warehouse) {
                    WarehouseWindow.main();
                } else {
                    StoreWindow.main();
                }

                this.setVisible(false);
            }
        } catch (DataAccessException e) {
            PopUp.newPopUp(this, e.getMessage(), "Database Error", PopUp.PopUpType.ERROR);
        } catch (ControlException e) {
            PopUp.newPopUp(this, e.getMessage(), "Invalid Credentials", PopUp.PopUpType.WARNING);
        }
    }
}
