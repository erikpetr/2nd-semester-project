package gui;

import controller.LoginController;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class WarehouseWindow extends JFrame {
    // Panel
    private final JPanel contentPanel;
    // Layout
    private final CardLayout cardLayout;

    // Cards

    public WarehouseWindow() {
        // Frame size
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(100, 100, (int) (screenSize.width * 0.8), (int) (screenSize.height * 0.8));

        // Create main panel
        JPanel panel = new JPanel();
        // Set borders
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        // Set Layout
        panel.setLayout(new BorderLayout(0, 0));

        // --- HEADER ---
        // Add header to the top of main panel
        panel.add(headerPanel(), BorderLayout.PAGE_START);
        // --- /HEADER ---

        // Inicialize content panel
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

        WarehouseDashboard dashboard = new WarehouseDashboard();
        contentPanel.add(dashboard, "dashboard");
        cardLayout.show(contentPanel, "dashboard");

        // Start layout monitor
        new LayoutMonitor().start();
    }

    /*
     * Launch the application.
     */
    public static void main() {
        EventQueue.invokeLater(() -> {
            WarehouseWindow frame = null;
            try {
                frame = new WarehouseWindow();
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
    private Component headerPanel() {
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
    private Component menuPanel() {
        JPanel menuPanel = new JPanel();
        // Set layout
        menuPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));

        // Buttons
        JButton btnOrders = ColorStyle.newButton("Orders");
        btnOrders.addActionListener(arg0 -> openOrdersMenu());
        menuPanel.add(btnOrders);

        JButton btnStocks = ColorStyle.newButton("Stocks");
        btnStocks.addActionListener(arg0 -> openStocksMenu());
        menuPanel.add(btnStocks);

        JButton btnWarehouseOrders = ColorStyle.newButton("Warehouse orders");
        btnWarehouseOrders.addActionListener(arg0 -> openWarehouseOrdersMenu());
        menuPanel.add(btnWarehouseOrders);

        JButton btnStores = ColorStyle.newButton("Stores");
        btnStores.addActionListener(arg0 -> openStoresMenu());
        menuPanel.add(btnStores);

        JButton btnProducts = ColorStyle.newButton("Products");
        btnProducts.addActionListener(arg0 -> openProductMenu());
        menuPanel.add(btnProducts);

        JButton btnProviders = ColorStyle.newButton("Providers");
        btnProviders.addActionListener(arg0 -> openProvidersMenu());
        menuPanel.add(btnProviders);

        JButton btnStatistics = ColorStyle.newButton("Stock Reports");
        btnStatistics.addActionListener(arg0 -> openStoreStockReportMenu());
        menuPanel.add(btnStatistics);

        return menuPanel;
    }

    /*
     * Creates account panel
     */
    private Component accountPanel() {
        JPanel account = new JPanel();

        JLabel user = new JLabel("Logged in as: " + LoginController.getLoggedInUser().getName());
        JButton logout = ColorStyle.newButton("Logout");
        logout.addActionListener(e -> {
            LoginController.logOut();
            this.setVisible(false);
            LoginMenu.main(null);
        });
        account.add(logout);
        user.setForeground(new Color(47, 36, 131));
        account.add(user);

        return account;
    }

    /*
     * Open orders card
     */
    public void openOrdersMenu() {

        WarehouseOrderMenu ordersCard = new WarehouseOrderMenu();
        LayoutChangeMonitor.getInstance().setLayout(ordersCard, "orders");
    }

    public void openWarehouseOrdersMenu() {
        WarehouseWarehouseOrderMenu warehouseWarehouseOrderMenu = new WarehouseWarehouseOrderMenu();
        LayoutChangeMonitor.getInstance().setLayout(warehouseWarehouseOrderMenu, "warehouse_orders");
    }

    /*
     * Open Stock card
     */
    public void openStocksMenu() {
        EventQueue.invokeLater(() -> {
            StockMenu stockCard = new StockMenu();
            LayoutChangeMonitor.getInstance().setLayout(stockCard, "stock");
        });
    }

    /*
     * Open stores card
     */
    public void openStoresMenu() {
        EventQueue.invokeLater(() -> {
            StoreMenu storesCard = new StoreMenu();
            LayoutChangeMonitor.getInstance().setLayout(storesCard, "stores");
        });
    }

    /*
     * Open products card
     */
    public void openProductMenu() {
        EventQueue.invokeLater(() -> {
            ProductMenu productsCard = new ProductMenu();
            LayoutChangeMonitor.getInstance().setLayout(productsCard, "products");
        });
    }

    /*
     * Open providers card
     */
    public void openProvidersMenu() {
        EventQueue.invokeLater(() -> {
            ProviderMenu providersCard = new ProviderMenu();
            LayoutChangeMonitor.getInstance().setLayout(providersCard, "providers");
        });
    }

    /*
     * Open store stock report card
     */
    public void openStoreStockReportMenu() {
        EventQueue.invokeLater(() -> {
            StoreStockReportMenuWarehouse storeStockReportsCard = new StoreStockReportMenuWarehouse();
            LayoutChangeMonitor.getInstance().setLayout(storeStockReportsCard, "store_stock_report");
        });
    }

    /*
     * Layout monitor
     */
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
