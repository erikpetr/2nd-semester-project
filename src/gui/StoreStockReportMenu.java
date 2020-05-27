package gui;

import java.awt.Color;

import java.util.LinkedList;
import java.util.List;

import controller.ControlException;
import controller.StoreStockReportController;
import java.awt.*;
import javax.swing.*;
import model.StoreStockReport;
import model.StoreStockReportItem;


public class StoreStockReportMenu extends JPanel {

    private List<StoreStockReport> reports = new LinkedList<>();
    private JPanel optionsPanel;
    private JList<StoreStockReport> reportList;
    private JTable itemTable;

    /**
     * Create the panel.
     */
    public StoreStockReportMenu () {
        this.loadReports();

        setLayout(new BorderLayout(0, 0));

        // Option Panel
        optionsPanel = new JPanel();
        this.add(optionsPanel, BorderLayout.NORTH);
        
        // Button Create
        JButton btnCreateReport = new JButton("Create Stock Report");
        optionsPanel.add(btnCreateReport);
        btnCreateReport.addActionListener(actionEvent -> {
            openCreateStoreStockMenu();
        });

        // Button Actualize
        JButton btnActualize = new JButton("Actualize");
        optionsPanel.add(btnActualize);
        btnActualize.addActionListener(actionEvent -> {
            this.loadReports();
        });

        // Report Split Panel
        JSplitPane reportPane= new JSplitPane();
        reportPane.setResizeWeight(0.5);
        this.add(reportPane, BorderLayout.CENTER);
        // reportPane.setContinuousLayout(true);

        // List Reports
        reportList = getReportJList();
        reportList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                reportPane.setRightComponent(getStoreStockReportTable(reportList.getSelectedValue()));
                this.revalidate();
            }
        });
        reportList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportPane.setLeftComponent(reportList);

        // Item Table
        itemTable = new JTable();
        reportPane.setRightComponent(itemTable);

        this.setVisible(true);
    }

    private void loadReports () {
        try {
            reports = StoreStockReportController.getReportStoreStockReportByStore();
        } catch (ControlException e) {
            PopUp.newPopUp(this, e.getMessage(), "Error", PopUp.PopUpType.WARNING);
        }
    }

    private JList<StoreStockReport> getReportJList() {
        JList<StoreStockReport> jList = new JList<>(reports.toArray(new StoreStockReport[reports.size()]));
        return jList;
    }

    /**
     * Get {@link JTable} of a storeStockReport
     *
     * @param report Report to display entirely
     */
    private JTable getStoreStockReportTable (StoreStockReport report) {
        String[] columnNames = {"ProductID",
            "Product",
            "Quantity",
            "Price",
            "Weight"
        };
        // Convert data to 3d array
        Object[][] alldata = new Object[report.getItems().size()][];
        int i = 0;
        for (StoreStockReportItem row : report.getItems()) {
            Object[] data = {
                row.getProduct().getId(),
                row.getProduct().getName(),
                row.getQuantity(),
                row.getProduct().getPrice(),
                row.getProduct().getWeight(),
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
        // Disable selection
        table.setRowSelectionAllowed(false);
        // Set border
        table.setBorder(BorderFactory.createLineBorder(Color.blue));
        return table;
    }

    public void openCreateStoreStockMenu () {
        JComponent createStoreStockReports = new CreateStoreStockReportMenu();
        LayoutChangeMonitor.getInstance().setLayout(createStoreStockReports, "createStoreStockMenu");
    }
}
