package gui;

import controller.ControlException;
import controller.LoginController;
import controller.WarehouseOrderController;
import database.DataAccessException;
import model.User;
import model.Warehouse;
import model.WarehouseOrder;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.util.List;

public class WarehouseWarehouseOrderMenu extends JPanel {
    JScrollPane scroll_warehouse_orders;
    JComponent warehouseOrderInfo;
    private WarehouseOrderController warehouseOrderController;

    /*
     * Create the panel.
     */
    public WarehouseWarehouseOrderMenu()
    {
        try {
            warehouseOrderController = new WarehouseOrderController();
        } catch (DataAccessException e) {
            PopUp.newPopUp(this, e.getMessage(), "Error", PopUp.PopUpType.ERROR);
        }

        // Set layout
        GridLayout gridLayout = new GridLayout(1,2);
        gridLayout.setHgap(10);
        setLayout(gridLayout);

        User user = LoginController.getLoggedInUser();

        if (user instanceof Warehouse) {
            JPanel optionsPane = new JPanel();
            add(optionsPane, BorderLayout.NORTH);

            JButton btnCreateWarehouseOrder = new JButton("Create Warehouse Order");
            optionsPane.add(btnCreateWarehouseOrder);
            btnCreateWarehouseOrder.addActionListener(actionEvent -> createWarehouseOrder());
        }

        // List of orders
        scroll_warehouse_orders = new JScrollPane(warehouseOrdersTable());
        add(scroll_warehouse_orders);

        warehouseOrderInfo = new WarehouseOrderInformationMenu();
        add(warehouseOrderInfo);
    }

    private void createWarehouseOrder() {
        CreateWarehouseOrderMenu component = new CreateWarehouseOrderMenu();
        LayoutChangeMonitor.getInstance().setLayout(component, "create_warehouse_order_menu");
    }

    /*
     * Create table and fetch data
     */
    private JTable warehouseOrdersTable() {
        // Column names
        String[] columnNames = {"ID",
                "Date",
                "Provider",
                "Status",
                ""};

        // Create table with row edit disable
        try {
            // Convert data to 3d array
            List<WarehouseOrder> warehouseOrders = null;
            try {
                warehouseOrders = new WarehouseOrderController().getWarehouseOrders();
            } catch (DataAccessException e1) {
                PopUp.newPopUp(getParent(), e1.getMessage(), "Error", PopUp.PopUpType.ERROR);
            }
            Object[][] alldata = new Object[warehouseOrders.size()][];
            int i = 0;
            // TODO: decide what to show
            for (WarehouseOrder row : warehouseOrders)
            {
                Object[] data = {
                        row.getId(),
                        row.getDate(),
                        row.getProvider(),
                        row.getStatus(),
                        "See more"
                };

                alldata[i] = data;
                i++;
            }

            JTable table = new JTable(alldata, columnNames) {
                @Override
                public boolean editCellAt(int row, int column, java.util.EventObject e) {
                    return false;
                }
            };

            // Activation selection
            //table.setRowSelectionAllowed(false);
            table.setCellSelectionEnabled(true);

            ListSelectionModel cellSelectionModel = table.getSelectionModel();
            cellSelectionModel.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

            cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
                @Override
                public void valueChanged(ListSelectionEvent arg0)
                {
                    String selected = null;

                    int[] selectedRow = table.getSelectedRows();
                    int[] selectedColumns = table.getSelectedColumns();

                    for (int i = 0; i < selectedRow.length; i++) {
                        for (int j = 0; j < selectedColumns.length; j++) {
                            // Get click listener at column "see more"
                            if (selectedColumns[j] == 4)
                            {
                                // Set values adjust to true
                                if (!arg0.getValueIsAdjusting())
                                {
                                    // Get id of order and open details
                                    openWarehouseOrder(table.getValueAt(selectedRow[i], 0));
                                }
                            }
                        }
                    }
                }

                private void openWarehouseOrder(Object valueAt)
                {
                    if (warehouseOrderInfo != null)
                    {
                        System.out.println("Removing warehouse order info");
                        // Remove old panel
                        remove(warehouseOrderInfo);
                        revalidate();
                        repaint();
                    }

                    // Create new panel
                    warehouseOrderInfo = new WarehouseOrderInformationMenu((int) valueAt);
                    // Add panel
                    add(warehouseOrderInfo);
                }
            });

            // Set border
            table.setBorder(BorderFactory.createLineBorder(Color.blue));

            return table;

        } catch (ControlException e) {
            PopUp.newPopUp(this, e.getMessage(), "Error", PopUp.PopUpType.WARNING);
        }
        return null;
    }
}
