package gui;

import controller.LoginController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StoreWindow extends JFrame {

	private final JPanel contentPanel;
	private final CardLayout cardLayout;

	/**
	 * Create the frame.
	 */
	public StoreWindow() {
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setBounds(100, 100, 1024, 512);
		JPanel mainPane = new JPanel();
		contentPanel = new JPanel();
		mainPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(mainPane);
		mainPane.setLayout(new BorderLayout(0, 0));

		JPanel header = new JPanel();
		header.setBorder(BorderFactory.createLineBorder(Color.black));
		mainPane.add(header, BorderLayout.NORTH);
		header.setLayout(new BorderLayout());

		JPanel logo = new JPanel();
		header.add(logo, BorderLayout.WEST);
		logo.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		logo.setLayout(new FlowLayout());

		JLabel tevosTitle = new JLabel("TEVOS");
		tevosTitle.setForeground(new Color(47, 36, 131));
		logo.add(tevosTitle);

		JLabel tevosSubtitle = new JLabel("Management System");
		tevosSubtitle.setForeground(new Color(47, 36, 131));
		logo.add(tevosSubtitle);
		JPanel menuPanel = new JPanel();
		header.add(menuPanel, BorderLayout.CENTER);

		JButton btnDashboard = new JButton("Dashboard");
		btnDashboard.addActionListener(arg0 -> openDashboardMenu());
		menuPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		menuPanel.add(btnDashboard);

		JButton btnOrders = new JButton("Orders");
		btnOrders.addActionListener(arg0 -> openOrdersMenu());
		menuPanel.add(btnOrders);

		JButton btnReports = new JButton("Stock Report");
		btnReports.addActionListener(arg0 -> openStoreStockReportMenu());
		menuPanel.add(btnReports);

		JPanel account = new JPanel();
		header.add(account, BorderLayout.EAST);

		JLabel user = new JLabel("Logged in as: " + LoginController.getLoggedInUser().getName());
		user.setForeground(new Color(47, 36, 131));
		account.add(user);
		mainPane.add(contentPanel);

		cardLayout = new CardLayout(0, 0);
		contentPanel.setLayout(cardLayout);

		JComponent dashboard = new JPanel();
		contentPanel.add(dashboard, "dashboard");

		// TODO: Add stuff for making StockReport and Store CRUD

		cardLayout.show(contentPanel, "dashboard");
		new LayoutMonitor().start();
	}

	/**
	 * Launch the application.
	 */
	public static void main() {
		EventQueue.invokeLater(() -> {
			StoreWindow frame = null;
			try {
				frame = new StoreWindow();
				frame.setTitle("Tevos Management System - Logged in as: " + LoginController.getLoggedInUser().getName());
				frame.setVisible(true);
			} catch (Exception e) {
				PopUp.newPopUp(frame, "Couldn't start the application.\nThere has been a catastrophic error.\nThe program will terminate\n" +
						e.getMessage(), "Failure", PopUp.PopUpType.ERROR);
				System.exit(-1);
			}
		});
	}

	public void openDashboardMenu() {
		cardLayout.show(contentPanel, "dashboard");
	}

	public void openOrdersMenu() {
		JComponent orders = new StoreOrderMenu();
		LayoutChangeMonitor.getInstance().setLayout(orders, "orders");
	}

	public void openStoreStockReportMenu() {
		JComponent storeStockReports = new StoreStockReportMenu();
		LayoutChangeMonitor.getInstance().setLayout(storeStockReports, "storeStockReport");
	}

	private class LayoutMonitor extends Thread {
		@Override
		public void run() {
			LayoutChangeMonitor monitor = LayoutChangeMonitor.getInstance();
			while (true) {
				JComponent component = monitor.getLayout();
				String componentName = monitor.getConstrains();
				contentPanel.add(component, componentName);
				cardLayout.show(contentPanel, componentName);
			}
		}
	}
}
