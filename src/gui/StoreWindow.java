package gui;

import controller.LoginController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class StoreWindow extends JFrame 
{
	// Panel
	private final JPanel contentPanel;
	// Layout
	private final CardLayout cardLayout;

	
	public StoreWindow()
	{
		// Frame size
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		setBounds(100, 100, (int) (screenSize.width * 0.8), (int) (screenSize.height * 0.8));
		
		// Create main panel
		JPanel panel = new JPanel();
		// Set borders
		panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		// Set Layout
		panel.setLayout(new BorderLayout(0,0));
		
		// --- HEADER ---
		// Add header to the top of main panel
		panel.add(headerPanel(), BorderLayout.PAGE_START);
		// --- /HEADER ---
		
		// Initialize content panel
		contentPanel = new JPanel();
		// Set layout
		cardLayout = new CardLayout(0, 0);
		contentPanel.setLayout(cardLayout);
		// Add to panel
		panel.add(contentPanel);
		
		// Set operation close of JFrame
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		// Set content pane
		setContentPane(panel);

		StoreDashboard dashboardCard = new StoreDashboard();
		contentPanel.add(dashboardCard,"dashboard");
		cardLayout.show(contentPanel, "dashboard");
		
		// Start layout monitor
		new LayoutMonitor().start();
	}

	/*
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
	
	/*
	 * Creates HEADER panel
	 */
	private Component headerPanel()
	{
		// Create panel
		JPanel header = new JPanel();
		// Set border
		header.setBorder(BorderFactory.createLineBorder(Color.black));
		// Set layout
		header.setLayout(new BorderLayout());
		
		// Create logo panel
		JPanel logo = new JPanel();
		// Add to header panel
		header.add(logo, BorderLayout.WEST);
		// Set layout and orientation
		logo.setLayout(new FlowLayout());
		logo.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
		
		// Title
		JLabel tevosTitle = new JLabel("TEVOS");
		tevosTitle.setForeground(new Color(47, 36, 131));
		logo.add(tevosTitle);
		
		// Subtitle
		JLabel tevosSubtitle = new JLabel("Management System");
		tevosSubtitle.setForeground(new Color(47, 36, 131));
		logo.add(tevosSubtitle);
		
		// Menu
		header.add(menuPanel(), BorderLayout.CENTER);
		
		// Account
		header.add(accountPanel(), BorderLayout.EAST);
		
		return header;
	}
	
	/*
	 * Creates menu panel
	 */
	private Component menuPanel()
	{
		JPanel menuPanel = new JPanel();
		// Set layout
		menuPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
		
		// Buttons
		JButton btnDashboard = ColorStyle.newButton("Dashboard");
		btnDashboard.addActionListener(arg0 -> openDashboardMenu());
		menuPanel.add(btnDashboard);

		JButton btnOrders = ColorStyle.newButton("Orders");
		btnOrders.addActionListener(arg0 -> openOrdersMenu());
		menuPanel.add(btnOrders);

		JButton btnStock = ColorStyle.newButton("Stock");
		btnStock.addActionListener(arg0 -> openStoreStockReportMenu());
		menuPanel.add(btnStock);
	
		return menuPanel;
	}
	
	/*
	 * Creates account panel
	 */
	private Component accountPanel()
	{
		JPanel account = new JPanel();

		JLabel user = new JLabel("Logged in as: " + LoginController.getLoggedInUser().getName());
		user.setForeground(new Color(47, 36, 131));
		account.add(user);
		
		return account;
	}
	
	/*
	 * Open dashboard card
	 */
	public void openDashboardMenu() 
	{
		EventQueue.invokeLater(() -> {
			StoreDashboard dashboardCard = new StoreDashboard();
			LayoutChangeMonitor.getInstance().setLayout(dashboardCard, "dashboard");
		});
	}
	
	/*
	 * Open orders card
	 */
	public void openOrdersMenu() 
	{
		EventQueue.invokeLater(() -> {
			StoreOrderMenu ordersCard = new StoreOrderMenu();
			LayoutChangeMonitor.getInstance().setLayout(ordersCard, "orders");
		});
	}

	/*
	 * Open store stock report card 
	 */
	public void openStoreStockReportMenu() 
	{
		EventQueue.invokeLater(() -> {
			StoreStockReportMenuStore storeStockReportsCard = new StoreStockReportMenuStore();
			LayoutChangeMonitor.getInstance().setLayout(storeStockReportsCard, "store_stock_report");
		});
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
				//System.out.println("Component shown: "+ componentName);
			}
		}
	}
}
