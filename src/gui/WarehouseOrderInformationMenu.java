package gui;

import controller.WarehouseOrderController;
import database.DataAccessException;
import model.WarehouseOrder;
import model.WarehouseOrderItem;
import model.WarehouseOrderRevision;

import javax.swing.*;
import java.awt.*;

public class WarehouseOrderInformationMenu extends JPanel {
    private WarehouseOrder warehouseOrder;
    private JPanel panel;

    // TODO: Add some fancy looking warehouse order information

    public WarehouseOrderInformationMenu(int warehouseOrderId)
    {
        //Get order
        try {
            warehouseOrder = new WarehouseOrderController().getWarehouseOrder(warehouseOrderId);
        } catch (DataAccessException e) {
            PopUp.newPopUp(this, e.getMessage(), "Error", PopUp.PopUpType.ERROR);
        }

        //System.out.println("Showing order number " + warehouseOrder.getId());

        // Main panel
        panel = new JPanel();
        //panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setLayout(new BorderLayout());

        // --- HEADER ---
        JPanel header = new JPanel();
        FlowLayout flowLayout = (FlowLayout) header.getLayout();
        flowLayout.setAlignOnBaseline(true);
        flowLayout.setVgap(10);
        flowLayout.setHgap(10);

        // Set border
        //header.setBorder(BorderFactory.createLineBorder(Color.black));

        // title
        header.add(titlePanel());
        // Add header to main panel
        panel.add(header, BorderLayout.NORTH);
        // --- /HEADER ---

        // --- BODY ---
        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane();
        //scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Body panel
        JPanel body = new JPanel();
        body.setLayout(new GridLayout(0,1));
        // Add body panel to scroll panel
        scrollPane.setViewportView(body);

        // Order details
        body.add(warehouseOrderDetails());

        // Order items
        body.add(warehouseOrderItems());

        // Revisions
        body.add(warehouseOrderRevisions());
        // --- /BODY ---*/

        add(panel);
        // Set visible
        setVisible(true);
    }

    public WarehouseOrderInformationMenu()
    {
        JLabel noresults = new JLabel("No warehouse order selected.");
        add(noresults);

        setVisible(true);
    }

    /*
     * Title panel for window
     */
    private Component titlePanel()
    {
        // Title label
        return new JLabel("WAREHOUSE ORDER DESCRIPTION (WAREHOUSE ORDER ID : " + warehouseOrder.getId() + ")");
    }

    /*
     * Creates panel for main details
     */

    // TODO: decide what to show

    private Component warehouseOrderDetails()
    {
        // Details panel
        JPanel details = new JPanel();
        FlowLayout flDetails = new FlowLayout(FlowLayout.CENTER, 50, 30);
        details.setLayout(flDetails);
        // labels
        JLabel date = new JLabel("Date: " + warehouseOrder.getDate());
        details.add(date);
        JLabel warehouse = new JLabel("Warehouse: " + warehouseOrder.getWarehouse());
        details.add(warehouse);
        JLabel provider = new JLabel("Provider: " + warehouseOrder.getProvider());
        details.add(provider);
        JLabel price = new JLabel("Total Price: " + warehouseOrder.calculateTotalPrice());
        details.add(price);

        return details;
    }

    /*
     * Creates scroll panel for order items
     */
    private Component warehouseOrderItems()
    {
        JPanel items = new JPanel();

        // Title
        JLabel title = new JLabel("Items");
        //title_items.setHorizontalAlignment(SwingConstants.CENTER);
        items.add(title);

        if (!warehouseOrder.getItems().isEmpty())
        {
            // Column names
            String[] columnNames = {
                    "Product",
                    "Quantity",
                    "Unit Price"
            };

            // Convert items list
            Object[][] alldata = new Object[warehouseOrder.getItems().size()][];
            int i = 0;
            for (WarehouseOrderItem row : warehouseOrder.getItems())
            {
                Object[] data = {
                        row.getProduct(),
                        row.getQuantity(),
                        row.getUnitPrice()
                };

                alldata[i] = data;
                i++;
            }

            // Items table
            JTable table = new JTable(alldata, columnNames) {
                @Override
                public boolean editCellAt(int row, int column, java.util.EventObject e) {
                    return false;
                }
            };

            // Remove row selection
            table.setRowSelectionAllowed(false);

            // Add table to scroll pane
            items.add(table);
        }
        else
        {
            JLabel noresults = new JLabel("No items on this warehouse order.");
            items.add(noresults);
        }

        return items;
    }
    /*
     * Creates panel for revisions
     */
    private Component warehouseOrderRevisions()
    {
        // Panel
        JPanel revisions = new JPanel();
        revisions.setLayout(new BoxLayout(revisions, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Revisions");
        revisions.add(title);

        for(WarehouseOrderRevision revision : warehouseOrder.getRevisions())
        {
            JLabel singleRevision = new JLabel(revision.toString());
            singleRevision.add(revisions);
        }

        return revisions;
    }
}
