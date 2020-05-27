package gui;

import java.awt.Color;

import java.util.LinkedList;
import java.util.List;

import controller.ControlException;
import controller.LoginController;
import controller.StoreStockReportController;

import model.Store;


import java.awt.*;
import javax.swing.*;
import model.StoreStockReport;
import model.StoreStockReportItem;


public abstract class StoreStockReportMenu extends JPanel {

    protected List<StoreStockReport> reports = new LinkedList<>();
    protected JPanel optionsPanel;
    protected JSplitPane reportPanel;
    protected JList<StoreStockReport> reportList;
    protected JTable itemTable;
    protected Store currentStore;

    /**
     * Create the panel.
     */
    protected StoreStockReportMenu () {
        setLayout(new BorderLayout(0, 0));

        // Option Panel
        optionsPanel = new JPanel();
        this.add(optionsPanel, BorderLayout.NORTH);

        // Button Actualize
        JButton btnActualize = new JButton("Actualize");
        optionsPanel.add(btnActualize);
        btnActualize.addActionListener(actionEvent -> {
            this.reloadDataAndGui();
        });
    }


    protected void refreshReportSplitPanel () {
        // Report Split Panel
        reportPanel = new JSplitPane();
        reportPanel.setResizeWeight(0.2);

        // List Reports
        reportList = getReportJList();
        reportList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                reportPanel.setRightComponent(getStoreStockReportTable(reportList.getSelectedValue()));
                this.revalidate();
            }
        });
        reportList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        reportPanel.setLeftComponent(reportList);

        // Item Table
        itemTable = new JTable();
        reportPanel.setRightComponent(itemTable);
    }

    protected JTable getStoreStockReportTable (StoreStockReport report) {
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

    protected JList<StoreStockReport> getReportJList() {
        JList<StoreStockReport> jList = new JList<>(reports.toArray(new StoreStockReport[reports.size()]));
        return jList;
    }

    protected abstract void reloadDataAndGui ();

    protected void loadReports () {
        try {
            reports = StoreStockReportController.getReportStoreStockReportByStore(currentStore);
        } catch (ControlException e) {
            PopUp.newPopUp(this, e.getMessage(), "Error", PopUp.PopUpType.WARNING);
        }
    }
}